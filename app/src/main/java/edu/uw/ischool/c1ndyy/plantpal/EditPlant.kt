package edu.uw.ischool.c1ndyy.plantpal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset


class EditPlant : AppCompatActivity() {

    data class PlantData(var plantIndex: Int, var notifSettingNum: Int, var heightGoal: Int, var currHeight: Int)

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var lastWatered : EditText
    lateinit var notifSetting : EditText
    lateinit var currHeight : EditText
    lateinit var heightGoal : EditText
    lateinit var saveChanges : Button
    lateinit var btnCancel : Button
    lateinit var btnChangeImg : Button
    private val handler = Handler(Looper.getMainLooper())
    private val NOTIFICATION_INTERVAL_KEY = "notification_interval"
    private val LAST_SHOWN_TIME_KEY = "last_shown_time"
    private lateinit var notifInterval: String

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
    private var selectedImageUri: Uri? = null
    private var originalImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editplant)

        val plantId = getIntent().getIntExtra("plantId", 0)
        Log.d("PlantInfo", "Plant ID: $plantId")
        val plantNameInfo = getIntent().getStringExtra("plantName")
        val plantTypeInfo = getIntent().getStringExtra("plantType")
        val careMethodInfo = getIntent().getStringExtra("careMethod")
        val lastWateredInfo = getIntent().getStringExtra("lastWatered")
        val notifyInfo = getIntent().getStringExtra("notify")
        val currHeightInfo = getIntent().getStringExtra("currHeight")
        val goalHeightInfo = getIntent().getStringExtra("goalHeight")

        val imageUri = intent.getStringExtra("imageUri")
        val imageView = findViewById<ImageView>(R.id.myImageView)
        if (imageUri != null && imageUri != "placeholder.png") {
            imageView.setImageURI(Uri.parse(imageUri))
        } else {
            imageView.setImageResource(R.drawable.placeholder)
        }

        val plantName = findViewById<TextView>(R.id.titlePlantName)
        plantName.setText(plantNameInfo)
        val plantType = findViewById<TextView>(R.id.textType)
        plantType.setText(plantTypeInfo)
        val careMethod = findViewById<TextView>(R.id.textCareMethod)
        careMethod.setText(careMethodInfo)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        lastWatered = findViewById(R.id.inputLastWatered)
        lastWatered.setText("${lastWateredInfo}")
        notifSetting = findViewById(R.id.inputNotifySetting)
        notifSetting.setText("${notifyInfo}")
        currHeight = findViewById(R.id.inputCurrHeight)
        currHeight.setText("${currHeightInfo}")
        heightGoal = findViewById(R.id.inputHeightGoal)
        heightGoal.setText("${goalHeightInfo}")

        saveChanges = findViewById(R.id.buttonSaveChanges)
        btnCancel = findViewById(R.id.buttonCancelEdit)
        btnChangeImg = findViewById(R.id.buttonChangeImg)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // make sure values are valid
                val allFieldsFilled = !notifSetting.text.isNullOrBlank() &&
                        !currHeight.text.isNullOrBlank() &&
                        !heightGoal.text.isNullOrBlank() &&
                        !lastWatered.text.isNullOrBlank() &&
                        isValidNum(lastWatered.text.toString()) &&
                        isValidNum(notifSetting.text.toString()) &&
                        isValidNum(currHeight.text.toString()) &&
                        isValidNum(heightGoal.text.toString())

                if (allFieldsFilled) {
                    lastWatered.error = null
                    notifSetting.error = null
                    currHeight.error = null
                    heightGoal.error = null
                    saveChanges.isEnabled = true
                } else {
                    if (!isValidNum(lastWatered.text.toString()) && lastWatered.text.isNotEmpty()) {
                        lastWatered.error = "Please enter a valid number in whole days."
                    }
                    if (!isValidNum(notifSetting.text.toString()) && notifSetting.text.isNotEmpty()) {
                        notifSetting.error = "Please enter a valid number in whole days."
                    }
                    if (!isValidNum(currHeight.text.toString()) && currHeight.text.isNotEmpty()) {
                        currHeight.error = "Please enter a positive whole integer in centimeters."
                    }
                    if (!isValidNum(heightGoal.text.toString()) && heightGoal.text.isNotEmpty()) {
                        heightGoal.error = "Please enter a positive whole integer in centimeters."
                    }
                    saveChanges.isEnabled = false
                }

            }
        }

        lastWatered.addTextChangedListener(textWatcher)
        notifSetting.addTextChangedListener(textWatcher)
        currHeight.addTextChangedListener(textWatcher)
        heightGoal.addTextChangedListener(textWatcher)

        btnChangeImg.setOnClickListener (View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        })

        btnCancel.setOnClickListener (View.OnClickListener {
            val originalImageUri = sharedPreferences.getString("imageUri_$plantId", "placeholder.png")
            sharedPreferences.edit().putString("imageUri_$plantId", originalImageUri).apply()

            val intent = Intent(this, PlantInfo::class.java)
            intent.putExtra("selectedImageUri", originalImageUri)
            intent.putExtra("inputWater", lastWateredInfo.toString().toInt())
            intent.putExtra("inputNotify", notifyInfo.toString().toInt())
            intent.putExtra("inputCurrHeight", currHeightInfo.toString().toInt())
            intent.putExtra("inputGoalHeight", goalHeightInfo.toString().toInt())
            startActivity(intent)
        })

        saveChanges.setOnClickListener (View.OnClickListener {
            val inputJustWatered = lastWatered.text.toString().toInt()
            val inputNotify = notifSetting.text.toString().toInt()
            val inputCurrHeight = currHeight.text.toString().toInt()
            val inputGoalHeight = heightGoal.text.toString().toInt()
            val imageUri = selectedImageUri?.toString() ?: "placeholder.png"

            updateJsonData(
                plantId.toString().toInt(),
                inputJustWatered,
                inputNotify,
                inputCurrHeight,
                inputGoalHeight,
                imageUri
            )

            notifInterval = notifSetting.text.toString()
            sharedPreferences.edit().putString(NOTIFICATION_INTERVAL_KEY, notifInterval).apply()
            scheduleToast()
            Log.d("Water Plant Reminder", "${plantName.text} set to every ${notifInterval} days")

            val intent = Intent(this, PlantInfo::class.java)
            intent.putExtra("selectedImageUri", imageUri)
            intent.putExtra("plant", plantId)
            intent.putExtra("inputWater", inputJustWatered)
            intent.putExtra("inputNotify", inputNotify)
            intent.putExtra("inputCurrHeight", inputCurrHeight)
            intent.putExtra("inputGoalHeight", inputGoalHeight)
            startActivity(intent)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                this.selectedImageUri = selectedImageUri

                val imageView = findViewById<ImageView>(R.id.myImageView)
                imageView.setImageURI(selectedImageUri)
            }
        }
    }

    fun isValidNum(input: String): Boolean {
        return try {
            val number = input.toLong()
            number >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun updateJsonData(plantId: Int, inputJustWatered: Int, inputNotify: Int, inputCurrHeight: Int, inputGoalHeight: Int, imageUri: String?) {
        val editor = sharedPreferences.edit()
        editor.putInt("inputJustWatered_$plantId", inputJustWatered)
        editor.putInt("inputNotify_$plantId", inputNotify)
        editor.putInt("inputCurrHeight_$plantId", inputCurrHeight)
        editor.putInt("inputGoalHeight_$plantId", inputGoalHeight)
        editor.putString("imageUri_$plantId", imageUri ?: "placeholder.png")
        editor.apply()

        Log.d("EditPlant", "Saved image URI for plant $plantId: $imageUri")

        val jsonString = sharedPreferences.getString("PlantData", null)

        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")

                if (id == plantId) {
                    jsonObject.put("lastWatered", inputJustWatered)
                    jsonObject.put("notify", inputNotify)
                    jsonObject.put("currHeight", inputCurrHeight)
                    jsonObject.put("goalHeight", inputGoalHeight)
                    jsonObject.put("imageUri", imageUri ?: "placeholder.png")

                    val updatedJsonString = jsonArray.toString()
                    writeJsonToFile(updatedJsonString)
                    break
                }
            }
        }
    }

    private fun writeJsonToFile(jsonString: String) {
        //write the json data back to the file
        val outputStream: OutputStream = openFileOutput("plants.json", Context.MODE_PRIVATE)
        outputStream.write(jsonString.toByteArray())
        outputStream.close()
        Log.d("SaveToJson", "Updated JSON file")
    }

    private fun scheduleToast() {
        val interval = notifInterval.toLongOrNull()

        if (interval != null && interval > 0) {
            val lastShownTime = sharedPreferences.getLong(LAST_SHOWN_TIME_KEY, 0)
            val nextShowTime = lastShownTime + interval * 24 * 60 * 60 * 1000
            val currentTime = System.currentTimeMillis()
            val plantName = findViewById<TextView>(R.id.titlePlantName)

            if (currentTime >= nextShowTime) {
                Toast.makeText(this, "Reminder: Time to water your ${plantName.text} plant!", Toast.LENGTH_LONG).show()

                sharedPreferences.edit().putLong(LAST_SHOWN_TIME_KEY, currentTime).apply()
            }
        }
    }

}