package edu.uw.ischool.c1ndyy.plantpal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlEditText: EditText
    private lateinit var updateFrequencyEditText: EditText
    private lateinit var savePreferencesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        urlEditText = findViewById(R.id.urlEditText)
        updateFrequencyEditText = findViewById(R.id.updateFrequencyEditText)
        savePreferencesButton = findViewById(R.id.savePreferencesButton)

        savePreferencesButton.setOnClickListener {
            if (savePreferences()) {
                loadPreferencesAndFetchData()
            }
        }
    }

    private fun savePreferences(): Boolean {
        val url = urlEditText.text.toString()
        val updateFrequency = updateFrequencyEditText.text.toString().toIntOrNull() ?: 0

        if (url.isBlank() || updateFrequency <= 0) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            return false
        }

        val editor = sharedPreferences.edit()
        editor.putString("URL", url)
        editor.putInt("UpdateFrequency", updateFrequency)
        editor.apply()

        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "Preferences saved: URL - $url, Frequency - $updateFrequency")
        return true
    }

    private fun loadPreferencesAndFetchData() {
        Log.d("MainActivity", "loadPreferencesAndFetchData called")
        val url = sharedPreferences.getString("URL", "https://docs.google.com/document/d/1Sb-4joMlhf_RO9OOd7QSzSybDGnQFOJ3RlICkFxIcXA/edit") ?: "https://docs.google.com/document/d/1Sb-4joMlhf_RO9OOd7QSzSybDGnQFOJ3RlICkFxIcXA/edit"
        val plantApp = application as PlantPal
        plantApp.downloadAndUpdateData(url) { fetchedData ->
            Log.d("MainActivity", "Data fetched: $fetchedData")
        }
    }
}