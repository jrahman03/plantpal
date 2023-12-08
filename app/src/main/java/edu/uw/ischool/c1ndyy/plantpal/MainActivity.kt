package edu.uw.ischool.c1ndyy.plantpal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import org.json.JSONArray
import java.io.IOException
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlEditText: EditText
    private lateinit var savePreferencesButton: Button
    private lateinit var settingsButton: Button
    private lateinit var preferencesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        urlEditText = findViewById(R.id.urlEditText)
        savePreferencesButton = findViewById(R.id.savePreferencesButton)
        settingsButton = findViewById(R.id.settings)
        preferencesLayout = findViewById(R.id.preferencesLayout)

        settingsButton.setOnClickListener {
            showPreferencesUI()
        }

        savePreferencesButton.setOnClickListener {
            if (savePreferences()) {
                val filename = sharedPreferences.getString("Filename", "plants.json") ?: "plants.json"
                val jsonString = getJsonDataFromAsset(filename)
                if (jsonString != null) {
                    parseJsonData(jsonString)
                }
            }
        }
    }

    private fun savePreferences(): Boolean {
        val filename = urlEditText.text.toString()

        if (filename.isBlank()) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            return false
        }

        val editor = sharedPreferences.edit()
        editor.putString("Filename", filename)
        editor.apply()

        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun showPreferencesUI() {
        preferencesLayout.visibility = View.VISIBLE
    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

    private fun parseJsonData(jsonString: String) {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val latin = jsonObject.getString("latin")
            val family = jsonObject.getString("family")

            // Extracting array of common names
            val commonJsonArray = jsonObject.getJSONArray("common")
            val commonNames = mutableListOf<String>()
            for (j in 0 until commonJsonArray.length()) {
                commonNames.add(commonJsonArray.getString(j))
            }

            val category = jsonObject.getString("category")
            val origin = jsonObject.getString("origin")
            val climate = jsonObject.getString("climate")

            // Extracting nested JSON object for tempmax
            val tempMaxObject = jsonObject.getJSONObject("tempmax")
            val tempMaxCelsius = tempMaxObject.getDouble("celsius")
            val tempMaxFahrenheit = tempMaxObject.getDouble("fahrenheit")

            // Extracting nested JSON object for tempmin
            val tempMinObject = jsonObject.getJSONObject("tempmin")
            val tempMinCelsius = tempMinObject.getDouble("celsius")
            val tempMinFahrenheit = tempMinObject.getDouble("fahrenheit")

            val idealLight = jsonObject.getString("ideallight")
            val toleratedLight = jsonObject.getString("toleratedlight")
            val watering = jsonObject.getString("watering")

            // Extracting array of insects
            val insects = mutableListOf<String>()
            if (jsonObject.get("insects") is JSONArray) {
                val insectsJsonArray = jsonObject.getJSONArray("insects")
                for (j in 0 until insectsJsonArray.length()) {
                    insects.add(insectsJsonArray.getString(j))
                }
            } else {
                // Handle the case where "insects" is a string (e.g., "N/A")
                val singleInsect = jsonObject.getString("insects")
                if (singleInsect != "N/A") {
                    insects.add(singleInsect)
                }
            }

            val diseases = jsonObject.getString("diseases")

            // Extracting array of uses
            val uses = mutableListOf<String>()
            if (jsonObject.get("use") is JSONArray) {
                val useJsonArray = jsonObject.getJSONArray("use")
                for (j in 0 until useJsonArray.length()) {
                    uses.add(useJsonArray.getString(j))
                }
            } else {
                val singleUse = jsonObject.getString("use")
                uses.add(singleUse)
            }
            Log.d("JSON Parsing", "Plant ID: $id, Latin Name: $latin, Family: $family, Common Names: $commonNames, Category: $category")

        }
    }
}