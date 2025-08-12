package com.example.emotionvoiceapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val resultsTextView = findViewById<TextView>(R.id.resultsTextView)

        try {
            val file = File(getExternalFilesDir(null), "rezultate_emotii.txt")
            if (file.exists()) {
                val contents = file.readText()
                resultsTextView.text = contents
            } else {
                resultsTextView.text = "Nu există rezultate salvate încă."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Eroare la citirea fișierului!", Toast.LENGTH_LONG).show()
        }
    }
}
