package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.json.JSONArray
import java.io.IOException

class PlantInfo : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plantsList: List<Plant>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantinfo)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)

        val btnEditPlant = findViewById<Button>(R.id.buttonEditPlant)
        val btnBack = findViewById<Button>(R.id.buttonBack)


        val plantId = getIntent().getIntExtra("plant", 0)
        Log.d("PlantInfo", "Received Plant ID: $plantId")

        val plant = loadPlantsData()?.find { it.id == plantId }

        if (plant != null) {
            val plantName = findViewById<TextView>(R.id.titlePlantName)
            plantName.text = plant.latin

            val plantType = findViewById<TextView>(R.id.textType)
            plantType.text = "Plant Type: ${plant.family}"

            val careMethod = findViewById<TextView>(R.id.textCareMethod)
            careMethod.text = "Care Method: ${plant.watering}"

            val lastWatered = findViewById<TextView>(R.id.textLastWatered)
            lastWatered.text = "Last Watered: ${plant.lastWatered}"

            val notify = findViewById<TextView>(R.id.textNotifyWater)
            notify.text = "Notify Setting: ${plant.notify}"

            val currHeight = findViewById<TextView>(R.id.textCurrHeight)
            currHeight.text = "Current Height: ${plant.currHeight}"

            val goalHeight = findViewById<TextView>(R.id.textHeightGoal)
            goalHeight.text = "Goal Height: ${plant.goalHeight}"


            btnEditPlant.setOnClickListener {
                val intent = Intent(this, EditPlant::class.java)
                //intent.putExtra("plant", position) need to pass in all the data
                startActivity(intent)
            }

            btnBack.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.e("PlantInfo", "Plant not found for ID: $plantId")
        }
    }


    data class Plant(
        val id: Int,
        val latin: String,
        val family: String,
        val watering: String,
        val lastWatered: String,
        val notify: String,
        val currHeight: String,
        val goalHeight: String,
    )

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

    private fun loadPlantsData(): List<Plant>? {
        val filename = sharedPreferences.getString("Filename", "plants.json") ?: "plants.json"
        val jsonString = getJsonDataFromAsset(filename)
        return if (jsonString != null) {
            parseJsonData(jsonString)
        } else {
            null
        }
    }

    private fun parseJsonData(jsonString: String): List<Plant>  {
        val jsonArray = JSONArray(jsonString)
        val plantsList = mutableListOf<Plant>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val latin = jsonObject.getString("latin")
            val family = jsonObject.getString("family")
            val watering = jsonObject.getString("watering")
            val lastWatered = if (jsonObject.has("lastWatered")) {
                jsonObject.getString("lastWatered")
            } else {
                "0 days ago (default)"
            }
            val notify =  if (jsonObject.has("notify")) {
                jsonObject.getString("notify")
            } else {
                "every 0 days (default)"
            }
            val currHeight = if (jsonObject.has("currHeight")) {
                jsonObject.getString("currHeight")
            } else {
                "0 cm (default)"
            }
            val goalHeight = if (jsonObject.has("goalHeight")) {
                jsonObject.getString("goalHeight")
            } else {
                "0 cm (default)"
            }

            val plant = Plant(id, latin, family, watering, lastWatered, notify, currHeight, goalHeight)
            plantsList.add(plant)
        }
        return plantsList
    }
}