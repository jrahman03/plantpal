package edu.uw.ischool.c1ndyy.plantpal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast
import org.json.JSONArray
import java.io.IOException
import android.util.Log
import android.content.Intent
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlEditText: EditText
    private lateinit var savePreferencesButton: Button
    private lateinit var settingsButton: Button
    private lateinit var preferencesLayout: LinearLayout
    private lateinit var plantsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        urlEditText = findViewById(R.id.urlEditText)
        savePreferencesButton = findViewById(R.id.savePreferencesButton)
        settingsButton = findViewById(R.id.settings)
        preferencesLayout = findViewById(R.id.preferencesLayout)
        plantsListView = findViewById(R.id.plantsListView)

        loadPlantsDataFromSharedPreferences()

        val firstPlantLatinName = loadFirstPlantLatinName()
        val plantsList = listOf(firstPlantLatinName)
        val adapter = ArrayAdapter(this, R.layout.list_item_plant, R.id.textViewPlantItem, plantsList)
        plantsListView.adapter = adapter

        val navSettings = findViewById<Button>(R.id.settings)
        navSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val navHome = findViewById<Button>(R.id.home)
        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val navExplore = findViewById<Button>(R.id.explore)
        navExplore.setOnClickListener {
            val intent = Intent(this, ExplorePlant::class.java)
            intent.putStringArrayListExtra("commonNames", ArrayList(commonNames))
            startActivity(intent)
        }

        plantsListView.setOnItemClickListener{_, _, position, _ ->
            val jsonString = getJsonDataFromAsset("plants.json")
            if (jsonString != null) {
                val jsonArray = JSONArray(jsonString)
                if (position < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(position)
                    val plantId = jsonObject.getInt("id")

                    val intent = Intent(this, PlantInfo::class.java)
                    intent.putExtra("plantId", plantId)
                    startActivity(intent)
                }
            }
        }


        savePreferencesButton.setOnClickListener {
            if (savePreferences()) {
                loadPlantsData()
            }
        }

        if (intent.getBooleanExtra("showPreferences", false)) {
            showPreferencesUI();
        }

    }
    override fun onResume() {
        super.onResume()
        loadPlantsDataFromSharedPreferences()
    }

    private fun loadPlantsDataFromSharedPreferences() {
        val jsonString = sharedPreferences.getString("PlantData", null)

        if (jsonString != null) {
            parseJsonData(jsonString)
        }
    }

    private fun loadFirstPlantLatinName(): String {
        val filename = sharedPreferences.getString("Filename", "plants.json") ?: "plants.json"
        val jsonString = getJsonDataFromAsset(filename)
        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() > 0) {
                val firstJsonObject = jsonArray.getJSONObject(0)
                return firstJsonObject.getString("latin")
            }
        }
        return "No Plant Data"
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

        findViewById<TextView>(R.id.textViewYourPlants).visibility = View.GONE

        findViewById<ListView>(R.id.plantsListView).visibility = View.GONE

        findViewById<LinearLayout>(R.id.linearLayoutNavigation).visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.preferencesLayout).visibility = View.VISIBLE

    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

    private fun loadPlantsData() {
        val filename = sharedPreferences.getString("Filename", "plants.json") ?: "plants.json"
        val jsonString = getJsonDataFromAsset(filename)
        if (jsonString != null) {
            parseJsonData(jsonString)
        }
    }

    val commonNames = mutableListOf<String>()

    private fun parseJsonData(jsonString: String) {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val latin = jsonObject.getString("latin")
            val family = jsonObject.getString("family")

            // Extracting array of common names
            val commonJsonArray = jsonObject.getJSONArray("common")
            // val commonNames = mutableListOf<String>()
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