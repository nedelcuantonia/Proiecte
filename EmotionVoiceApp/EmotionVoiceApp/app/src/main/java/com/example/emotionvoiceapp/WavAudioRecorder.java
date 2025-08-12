package com.example.emotionvoiceapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavAudioRecorder {

    public static final int SAMPLE_RATE = 16000;
    public static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder;
    private int bufferSize;
    private Thread recordingThread;
    private boolean isRecording = false;
    private File wavFile;
    private Context context;
    private String outputPath;

    public enum State {
        INITIALIZING, READY, RECORDING, ERROR, STOPPED
    }

    private State state = State.INITIALIZING;

    public WavAudioRecorder(Context context, String path) {
        this.context = context;
        this.outputPath = path;

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, AUDIO_ENCODING);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Permisiunea RECORD_AUDIO nu este acordată");
        }

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, CHANNELS, AUDIO_ENCODING, bufferSize);

        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
            state = State.READY;
        } else {
            state = State.ERROR;
        }
    }

    public static WavAudioRecorder getInstance(Context context, String path) {
        return new WavAudioRecorder(context, path);
    }

    public void prepare() {
        // Poți adăuga inițializări suplimentare aici dacă e nevoie
        wavFile = new File(outputPath);
    }

    public void start() {
        if (state == State.READY) {
            recorder.startRecording();
            isRecording = true;
            state = State.RECORDING;

            recordingThread = new Thread(() -> writeAudioDataToFile(), "AudioRecorder Thread");
            recordingThread.start();
        }
    }

    public void stop() {
        if (state == State.RECORDING) {
            isRecording = false;
            recorder.stop();

            try {
                if (recordingThread != null) {
                    recordingThread.join();  //  Așteaptă thread-ul de scriere
                    recordingThread = null;
                }

                File rawFile = new File(outputPath);
                File finalWav = new File(outputPath.replace(".wav", "_final.wav"));
                addWavHeader(rawFile, finalWav);
                // rawFile.delete(); // optional

            } catch (IOException | InterruptedException e) {
                Log.e("WavRecorder", "Eroare la oprire/conversie: " + e.getMessage());
            }

            state = State.STOPPED;
        }
    }

    @SuppressLint("MissingPermission")

    public void reset() {
        release();

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, CHANNELS, AUDIO_ENCODING, bufferSize);

        state = (recorder.getState() == AudioRecord.STATE_INITIALIZED) ? State.READY : State.ERROR;
    }

    public void release() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
            state = State.INITIALIZING;
        }
    }

    public State getState() {
        return state;
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        FileOutputStream os;

        try {
            os = new FileOutputStream(outputPath);
        } catch (IOException e) {
            Log.e("WavRecorder", "Nu pot deschide fișierul de ieșire", e);
            return;
        }

        while (isRecording) {
            int read = recorder.read(data, 0, data.length);

            if (read > 0) {
                try {
                    os.write(data, 0, read);
                } catch (IOException e) {
                    Log.e("WavRecorder", "Eroare la scriere în fișier", e);
                }
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            Log.e("WavRecorder", "Eroare la închiderea fișierului", e);
        }
    }

    // Adaugă un header WAV la fișierul PCM
    public static void addWavHeader(File pcmFile, File wavFile) throws IOException {
        FileInputStream pcmInputStream = new FileInputStream(pcmFile);
        FileOutputStream wavOutputStream = new FileOutputStream(wavFile);

        long totalAudioLen = pcmInputStream.getChannel().size();
        long totalDataLen = totalAudioLen + 36;
        int sampleRate = SAMPLE_RATE; // ex: 16000
        int channels = 1; // MONO
        int bitsPerSample = 16;
        long byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        byte[] header = new byte[44];

        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0; // subchunk1 size
        header[20] = 1; header[21] = 0; // PCM format
        header[22] = (byte) channels; header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) blockAlign; header[33] = 0;
        header[34] = (byte) bitsPerSample; header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        wavOutputStream.write(header);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = pcmInputStream.read(buffer)) != -1) {
            wavOutputStream.write(buffer, 0, bytesRead);
        }

        pcmInputStream.close();
        wavOutputStream.close();
    }

}
