package edu.uw.ischool.c1ndyy.plantpal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.logging.Handler

class PlantInfo : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plantsList: List<Plant>
    private lateinit var handler: Handler
    private lateinit var updateLastWateredRunnable: Runnable

    var inputWater: Int = 0
    var inputNotify: Int = 0
    var inputCurrHeight: Int = 0
    var inputGoalHeight: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantinfo)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        /*
        inputWater = getIntent().getIntExtra("inputWater", 0)
        inputNotify = getIntent().getIntExtra("inputNotify", 0)
        inputCurrHeight = getIntent().getIntExtra("inputCurrHeight", 0)
        inputGoalHeight = getIntent().getIntExtra("inputGoalHeight", 0)
         */

        val btnEditPlant = findViewById<Button>(R.id.buttonEditPlant)
        val btnBack = findViewById<Button>(R.id.buttonBack)
        val btnJustWatered = findViewById<Button>(R.id.buttonWatered)

        val selectedImageUriString = intent.getStringExtra("selectedImageUri")
        val selectedImageUri: Uri? = if (!selectedImageUriString.isNullOrEmpty()) {
            Uri.parse(selectedImageUriString)
        } else {
            null
        }

        val plantId = getIntent().getIntExtra("plant", 0)
        Log.d("PlantInfo", "Received Plant ID: $plantId")
        inputWater = sharedPreferences.getInt("inputJustWatered_$plantId", 0)
        inputNotify = sharedPreferences.getInt("inputNotify_$plantId", 0)
        inputCurrHeight = sharedPreferences.getInt("inputCurrHeight_$plantId", 0)
        inputGoalHeight = sharedPreferences.getInt("inputGoalHeight_$plantId", 0)

        val plant = loadPlantsData()?.find { it.id == plantId }

        if (plant != null) {
            val imageView = findViewById<ImageView>(R.id.myImageView)
            val imageUri = sharedPreferences.getString("imageUri_$plantId", "placeholder.png")
            if (imageUri == "placeholder.png") {
                imageView.setImageResource(R.drawable.placeholder) // Replace with your placeholder drawable resource
            } else {
                val imageUri = Uri.parse(imageUri)
                val contentResolver = contentResolver
                try {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageView.setImageResource(R.drawable.placeholder)
                }
            }
            Log.d("Plant Image", "Plant image link: $selectedImageUri")

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

            val currentTime = System.currentTimeMillis()
            val lastUpdateTime = sharedPreferences.getLong("lastUpdateTime_$plantId", 0)

            if (currentTime - lastUpdateTime >= 24 * 60 * 60 * 1000) {
                plant.lastWatered = (plant.lastWatered.toInt() + 1).toString()
                lastWatered.text = "Last Watered: ${plant.lastWatered} days ago"
                sharedPreferences.edit().putLong("lastUpdateTime_$plantId", currentTime).apply()
            }

            if (plant.currHeight == plant.goalHeight && plant.goalHeight != "0") {
                Toast.makeText(this, "$plantNameText has reached its height goal! Congratulations!", Toast.LENGTH_LONG).show()
            }

            btnJustWatered.setOnClickListener {
                Toast.makeText(this, "You just watered your plant. Updated plant info.", Toast.LENGTH_LONG).show()
                plant.lastWatered = "0"
                lastWatered.text = "Last Watered: 0 days ago"
                sharedPreferences.edit().putLong("lastUpdateTime_$plantId", System.currentTimeMillis()).apply()
            }

            btnEditPlant.setOnClickListener {
                val intent = Intent(this, EditPlant::class.java)
                intent.putExtra("imageUri", selectedImageUriString)
                Log.d("Plant Image", "Plant image link: ${selectedImageUriString}")
                intent.putExtra("plantId", plant.id)
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
                finish()
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
        var lastUpdateTime: Long,
        var imageUri: String? = "placeholder.png"
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
            val lastUpdateTime = if (jsonObject.has("lastUpdateTime")) {
                jsonObject.getLong("lastUpdateTime")
            } else {
                System.currentTimeMillis()
            }
            val imageUri = if (jsonObject.has("imageUri")) {
                jsonObject.getString("imageUri")
            } else {
                jsonObject.optString("imageUri", "placeholder.png")
            }

            val plant = Plant(id, latin, family, watering, lastWatered, notify, currHeight, goalHeight, lastUpdateTime, imageUri)
            plantsList.add(plant)
        }
        return plantsList
    }

}