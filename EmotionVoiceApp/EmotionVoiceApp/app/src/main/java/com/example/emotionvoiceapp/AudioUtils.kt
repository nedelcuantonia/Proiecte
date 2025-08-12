package com.example.emotionvoiceapp

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.Log

object AudioUtils {

    private fun convertToFloat(bytes: ByteArray, littleEndian: Boolean): Float {
        val buffer = ByteBuffer.wrap(bytes)
        buffer.order(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        return buffer.short.toFloat()
    }

    private fun ByteArrayToNumber(bytes: ByteArray, numOfBytes: Int, littleEndian: Boolean): ByteBuffer {
        val buffer = ByteBuffer.allocate(numOfBytes)
        buffer.order(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        buffer.put(bytes)
        buffer.rewind()
        return buffer
    }

    fun readSound(file: File): FloatArray {
        val type = listOf(false, true, false, false, true, true, true, true, true, true, true, false, true)
        val numberOfBytes = listOf(4, 4, 4, 4, 4, 2, 2, 4, 4, 2, 2, 4, 4)

        val inputStream: InputStream = FileInputStream(file)
        var numChannels = 1
        var bitsPerSample = 16

        for (i in numberOfBytes.indices) {
            val byteArray = ByteArray(numberOfBytes[i])
            inputStream.read(byteArray, 0, numberOfBytes[i])
            val buffer = ByteArrayToNumber(byteArray, numberOfBytes[i], type[i])
            if (i == 6) {
                numChannels = buffer.short.toInt()
            }
            if (i == 10) {
                bitsPerSample = buffer.short.toInt()
            }
            if (i == 11) {
                val id = String(byteArray)
                if (id != "data") {
                    val tempLength = ByteArray(4)
                    inputStream.read(tempLength)
                    val tempBuffer = ByteArrayToNumber(tempLength, 4, true)
                    val temp = tempBuffer.int
                    inputStream.skip(temp.toLong())
                    val nextID = ByteArray(4)
                    inputStream.read(nextID)
                }
            }
        }

        val bytePerSample = bitsPerSample / 8
        val dataList = mutableListOf<Float>()
        val bufferArray = ByteArray(bytePerSample)

        while (true) {
            val readBytes = inputStream.read(bufferArray, 0, bytePerSample)
            if (readBytes == -1) break
            val value = convertToFloat(bufferArray, true)
            dataList.add(value)
        }

        inputStream.close()

        val prag = 0.0001f
        val signal = if (numChannels == 1) {
            dataList.filter { kotlin.math.abs(it) >= prag }.toFloatArray()
        } else {
            val stereo = FloatArray(dataList.size / 2)
            for (i in stereo.indices) {
                stereo[i] = ((dataList[2 * i] + dataList[2 * i + 1]) / 2f)
            }
            stereo.filter { kotlin.math.abs(it) >= prag }.toFloatArray()
        }


        Log.d("READSOUND", "Signal size = ${signal.size}, min = ${signal.minOrNull()}, max = ${signal.maxOrNull()}")
        return signal
    }

    fun NRDT(signal: FloatArray, channels: IntArray): Array<FloatArray> {
        val w = 1024
        val delMax = w / 4
        val channelsFiltered = channels.filter { it <= delMax }
        val m = channelsFiltered.size

        val spectrograms = signal.size / w
        val samples = spectrograms * w
        val matrix = Array(spectrograms) { FloatArray(w) }

        var j = 0
        var i = 0
        while (i < samples && j < spectrograms) {
            for (k in 0 until w) {
                matrix[j][k] = signal[i]
                i++
            }
            j++
        }

        val spectrum = Array(m) { FloatArray(spectrograms) }
        val values = FloatArray(w)

        for (s in 0 until spectrograms) {
            for (k in 0 until w) {
                values[k] = matrix[s][k]
            }

            for ((k, delay) in channelsFiltered.withIndex()) {
                val diffs = FloatArray(w - 2 * delay - 1)
                var sum = 0f
                for (l in diffs.indices) {
                    val t = delay + l
                    val diff = kotlin.math.abs(values[t - delay] + values[t + delay] - 2 * values[t])
                    diffs[l] = diff
                    sum += diff
                }
                val mean = sum / diffs.size
                spectrum[k][s] = mean / 4
            }
        }
        return spectrum
    }

    fun getFeaturesNRDT(signal: FloatArray, channels: IntArray): Array<FloatArray> {
        val w = 1024
        val M = 40
        val delMax = w / 4
        val channelsFiltered = channels.filter { it <= delMax }
        val m = channelsFiltered.size

        if (signal.size < M * w) {
            Log.w("FEATURES", "Semnal prea scurt pentru NRDT: ${signal.size} < ${M * w}")
            return Array(M) { FloatArray(m) { 0f } }
        }

        val npsegm = signal.size / M
        val featSpec = Array(M) { FloatArray(m) }

        for (segm in 0 until M) {
            val ssegment = signal.sliceArray(segm * npsegm until (segm + 1) * npsegm)
            val spectrum = NRDT(ssegment, channelsFiltered.toIntArray())

            for (j in 0 until m) {
                featSpec[segm][j] = spectrum[j].sum()
            }
        }

        Log.d("FEATURES", "Primele valori: ${featSpec[0].joinToString()}")
        return featSpec
    }
}
