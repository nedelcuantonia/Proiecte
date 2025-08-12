package com.example.emotionvoiceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.media.MediaPlayer
import java.io.File
import java.io.IOException
import android.util.Log
import android.content.Intent
import android.app.Activity

class MainActivity : AppCompatActivity() {

    private lateinit var classifyButtonVres: Button
    private lateinit var classifyButtonVcnn: Button
    private lateinit var resultTextVres: TextView
    private lateinit var resultTextVcnn: TextView
    private lateinit var recordButton: Button
    private lateinit var playButton: Button
    private var mediaPlayer: MediaPlayer? = null
    private val CHOOSE_AUDIO_FILE = 101
    private var selectedAudioPath: String? = null

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var isRecording = false
    private lateinit var recorder: WavAudioRecorder
    private val outputPath by lazy {
        "${externalCacheDir?.absolutePath}/recorded.wav"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        classifyButtonVres = findViewById(R.id.classifyButtonVres)
        classifyButtonVcnn = findViewById(R.id.classifyButtonVcnn)
        resultTextVres = findViewById(R.id.resultTextVres)
        resultTextVcnn = findViewById(R.id.resultTextVcnn)
        recordButton = findViewById(R.id.recordButton)
        playButton = findViewById(R.id.playButton)

        classifyButtonVres.setOnClickListener {
            runInference("vres_model.tflite", "VRES-CNN", resultTextVres)
        }

        classifyButtonVcnn.setOnClickListener {
            runInference("vcnn_model.tflite", "V-CNN", resultTextVcnn)
        }

        recordButton.setOnClickListener {
            if (checkPermissions()) {
                if (!isRecording) {
                    recorder = WavAudioRecorder.getInstance(this, outputPath)

                    if (recorder.state == WavAudioRecorder.State.READY || recorder.state == WavAudioRecorder.State.INITIALIZING) {
                        Thread {
                            try {
                                recorder.prepare()
                                recorder.start()
                                runOnUiThread {
                                    isRecording = true
                                    recordButton.text = "OPREȘTE"
                                    Toast.makeText(this, "Înregistrare pornită", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                runOnUiThread {
                                    Toast.makeText(this, "Eroare la pornirea înregistrării!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    }
                } else {
                    Thread {
                        try {
                            if (::recorder.isInitialized && recorder.state == WavAudioRecorder.State.RECORDING) {
                                recorder.stop()
                                recorder.reset()
                            }

                            val rawFile = File(outputPath)
                            val wavFile = File(outputPath.replace(".wav", "_final.wav"))

                            if (!rawFile.exists() || rawFile.length() < 100) {
                                runOnUiThread {
                                    Toast.makeText(this, "Fișierul de înregistrare nu a fost creat sau este gol!", Toast.LENGTH_SHORT).show()
                                }
                                return@Thread
                            }

                            WavAudioRecorder.addWavHeader(rawFile, wavFile)

                            if (wavFile.exists() && wavFile.length() > 100) {
                                selectedAudioPath = wavFile.absolutePath
                                Log.d("INREGISTRARE", "Fișier WAV final: $selectedAudioPath")
                            } else {
                                Log.e("INREGISTRARE", "Fișierul WAV final nu a fost creat corect!")
                            }

                            runOnUiThread {
                                isRecording = false
                                recordButton.text = "ÎNREGISTREAZĂ"
                                Toast.makeText(this, "Înregistrare oprită și salvată (WAV)", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(this, "Eroare la oprirea înregistrării!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.start()
                }
            } else {
                requestPermissions()
            }
        }


        playButton.setOnClickListener {
            val audioPath = selectedAudioPath ?: outputPath.replace(".wav", "_final.wav")
            val audioFile = File(audioPath)

            if (audioFile.exists()) {
                if (mediaPlayer == null) {
                    try {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(audioFile.absolutePath)
                            prepare()
                            start()
                        }
                        Toast.makeText(this, "Redare pornită", Toast.LENGTH_SHORT).show()

                        mediaPlayer?.setOnCompletionListener {
                            mediaPlayer?.release()
                            mediaPlayer = null
                            Toast.makeText(this, "Redare finalizată", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Eroare la redare!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    Toast.makeText(this, "Redare oprită", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fișierul audio nu există la: $audioPath", Toast.LENGTH_SHORT).show()
            }
        }

        val loadAudioButton = findViewById<Button>(R.id.loadAudioButton)
        loadAudioButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, CHOOSE_AUDIO_FILE)
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun readWavAndExtractFeatures(filePath: String): Array<Array<Array<FloatArray>>> {
        val file = File(filePath)
        val signal = AudioUtils.readSound(file)
        val channels = intArrayOf(2, 4, 8, 16, 32, 64, 128)
        val features = AudioUtils.getFeaturesNRDT(signal, channels)

        // Normalizare pe toate valorile
        val maxVal = features.flatMap { it.asList() }.maxOrNull()?.takeIf { it != 0f } ?: 1f
        Log.d("FEATURES_NORM", "Valoare max = $maxVal")

        // Inițializare tensor normalizat
        val inputTensor = Array(1) { Array(40) { Array(7) { FloatArray(1) } } }

        for (i in 0 until 40) {
            for (j in 0 until 7) {
                inputTensor[0][i][j][0] = features[i][j] / maxVal
            }
        }

        Log.d("FEATURES_NORM", "Max = $maxVal, Primul element normalizat = ${inputTensor[0][0][0][0]}")
        return inputTensor
    }


    private fun runInference(
        modelName: String,
        modelLabel: String,
        targetTextView: TextView
    ) {
        try {
            val tfliteInference = TFLiteInference(assets, modelName)

            val audioPath = selectedAudioPath
                ?: outputPath.replace(".wav", "_final.wav").takeIf { File(it).exists() }

            if (audioPath == null) {
                Toast.makeText(this, "Nu există niciun fișier de procesat!", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d("INFERENCE_PATH", "Se folosește fișierul: $audioPath")

            val processedInput = readWavAndExtractFeatures(audioPath)
            Log.d("DEBUG_INFERENCE", "Input shape: ${processedInput.contentDeepToString()}")

            val flatInput = flattenInput(processedInput)

            Log.d("INFERENCE_INPUT", "Primele 10 valori: ${flatInput.take(10)}")
            Log.d("INFERENCE_INPUT", "Min: ${flatInput.minOrNull()}, Max: ${flatInput.maxOrNull()}, Avg: ${flatInput.average()}")

            val output = tfliteInference.runInference(processedInput)
            Log.d("DEBUG_INFERENCE", "Output: ${output.contentToString()}")
            val sum = output.sum()
            Log.d("INFERENCE_OUTPUT", "Sumă probabilități: $sum, Max: ${output.maxOrNull()}")

            val labels = listOf("Neutral", "Calm", "Happy", "Sad", "Angry", "Fear", "Disgust", "Surprise")
            val predictedClass = output.indices.maxByOrNull { output[it] } ?: -1
            val predictedLabel = if (predictedClass in labels.indices) labels[predictedClass] else "Necunoscută"

            val message = "[$modelLabel] Emoție prezisă: $predictedLabel"
            targetTextView.text = message
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            saveResultToFile(message)

            tfliteInference.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Eroare la rularea modelului $modelLabel!", Toast.LENGTH_LONG).show()
        }
    }
    private fun flattenInput(tensor: Array<Array<Array<FloatArray>>>): List<Float> {
        return tensor.flatMap { matrix ->  // Array<Array<FloatArray>>
            matrix.flatMap { row ->        // Array<FloatArray>
                row.map { it[0] }          // FloatArray -> extrag primul element (de fapt singurul)
            }
        }
    }

    private fun saveResultToFile(message: String) {
        try {
            val fileName = "rezultate_emotii.txt"
            val file = File(getExternalFilesDir(null), fileName)
            file.appendText("$message\n")
            Log.d("SAVE_RESULT", "Rezultat salvat în: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Eroare la salvarea rezultatului!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_AUDIO_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = try {
                    contentResolver.openInputStream(uri)
                } catch (e: Exception) {
                    Log.e("IMPORT_ERROR", "Eroare la deschiderea streamului", e)
                    null
                }

                if (inputStream != null) {
                    val tempFile = File.createTempFile("imported_audio", ".wav", cacheDir)
                    try {
                        inputStream.copyTo(tempFile.outputStream())
                        selectedAudioPath = tempFile.absolutePath
                        Toast.makeText(this, "Fișier importat cu succes!", Toast.LENGTH_SHORT).show()
                        Log.d("AUDIO_PATH", "Fișier salvat la: $selectedAudioPath")
                    } catch (e: Exception) {
                        Log.e("IMPORT_ERROR", "Eroare la copierea fișierului", e)
                        Toast.makeText(this, "Eroare la copierea fișierului.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fișierul nu a putut fi accesat. Încearcă să îl descarci în telefon.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
