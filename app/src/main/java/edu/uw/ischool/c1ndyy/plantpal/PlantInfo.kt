package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import java.io.IOException

class PlantInfo : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plantsList: List<Plant>

    var inputWater: Int = 0
    var inputNotify: Int = 0
    var inputCurrHeight: Int = 0
    var inputGoalHeight: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantinfo)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        inputWater = getIntent().getIntExtra("inputWater", 0)
        inputNotify = getIntent().getIntExtra("inputNotify", 0)
        inputCurrHeight = getIntent().getIntExtra("inputCurrHeight", 0)
        inputGoalHeight = getIntent().getIntExtra("inputGoalHeight", 0)

        val btnEditPlant = findViewById<Button>(R.id.buttonEditPlant)
        val btnBack = findViewById<Button>(R.id.buttonBack)
        val btnJustWatered = findViewById<Button>(R.id.buttonWatered)

        val plantId = getIntent().getIntExtra("plant", 0)
        Log.d("PlantInfo", "Received Plant ID: $plantId")

        val plant = loadPlantsData()?.find { it.id == plantId }

        if (plant != null) {
            val plantName = findViewById<TextView>(R.id.titlePlantName)
            val plantNameText = plant.latin
            plantName.text = plantNameText

            val plantType = findViewById<TextView>(R.id.textType)
            val plantTypeText = "Plant Type: ${plant.family}"
            plantType.text = plantTypeText

            val careMethod = findViewById<TextView>(R.id.textCareMethod)
            val careMethodText = "Care Method: ${plant.watering}"
            careMethod.text = careMethodText

            val lastWatered = findViewById<TextView>(R.id.textLastWatered)
            val lastWateredText = "Last Watered: ${plant.lastWatered} days ago"
            lastWatered.text = lastWateredText

            val notify = findViewById<TextView>(R.id.textNotifyWater)
            val notifyText = "Notify Setting: every ${plant.notify} days"
            notify.text = notifyText

            val currHeight = findViewById<TextView>(R.id.textCurrHeight)
            val currHeightText = "Current Height: ${plant.currHeight} cm"
            currHeight.text = currHeightText

            val goalHeight = findViewById<TextView>(R.id.textHeightGoal)
            val goalHeightText = "Goal Height: ${plant.goalHeight} cm"
            goalHeight.text = goalHeightText

            btnJustWatered.setOnClickListener {
                Toast.makeText(this, "You just watered your plant. Updated plant info.", Toast.LENGTH_LONG).show()
                lastWatered.text = "Last Watered: 0 days ago"
                plant.lastWatered = "0"
            }

            btnEditPlant.setOnClickListener {
                val intent = Intent(this, EditPlant::class.java)
                intent.putExtra("plantName", plantNameText)
                intent.putExtra("plantType", plantTypeText)
                intent.putExtra("careMethod", careMethodText)
                intent.putExtra("lastWatered", plant.lastWatered)
                intent.putExtra("notify", plant.notify)
                intent.putExtra("currHeight", plant.currHeight)
                intent.putExtra("goalHeight", plant.goalHeight)
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
        var lastWatered: String,
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
                inputWater.toString()
            }
            val notify =  if (jsonObject.has("notify")) {
                jsonObject.getString("notify")
            } else {
                inputNotify.toString()
            }
            val currHeight = if (jsonObject.has("currHeight")) {
                jsonObject.getString("currHeight")
            } else {
                inputCurrHeight.toString()
            }
            val goalHeight = if (jsonObject.has("goalHeight")) {
                jsonObject.getString("goalHeight")
            } else {
                inputGoalHeight.toString()
            }

            val plant = Plant(id, latin, family, watering, lastWatered, notify, currHeight, goalHeight)
            plantsList.add(plant)
        }
        return plantsList
    }
}