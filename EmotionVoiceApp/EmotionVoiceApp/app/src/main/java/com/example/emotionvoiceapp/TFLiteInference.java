package com.example.emotionvoiceapp;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TFLiteInference {

    private Interpreter tflite;

    // Constructor - încarcă modelul din assets
    public TFLiteInference(AssetManager assetManager, String modelPath) throws IOException {
        tflite = new Interpreter(loadModelFile(assetManager, modelPath));

        // DEBUG: Afișează forma tensorului de intrare
        int[] inputShape = tflite.getInputTensor(0).shape();
        Log.d("TFLiteInference", "Forma tensorului de intrare: " + Arrays.toString(inputShape));

        // DEBUG: Afișează forma tensorului de ieșire
        int[] outputShape = tflite.getOutputTensor(0).shape();
        Log.d("TFLiteInference", "Forma tensorului de ieșire: " + Arrays.toString(outputShape));
    }

    // Metodă pentru încărcarea modelului .tflite
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Metodă pentru rularea inferenței
    public float[] runInference(float[][][][] inputData) {
        float[][] output = new float[1][8];  // Presupunem că modelul returnează 8 clase
        tflite.run(inputData, output);

        // DEBUG: Afișează rezultatul inferenței
        Log.d("TFLiteInference", "Rezultatul inferenței: " + Arrays.toString(output[0]));

        return output[0];
    }

    // Eliberăm resursele
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}
