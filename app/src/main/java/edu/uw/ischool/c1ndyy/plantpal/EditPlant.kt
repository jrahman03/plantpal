package edu.uw.ischool.c1ndyy.plantpal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var plantName: String
    private val plantDataMap: MutableMap<String, PlantData> = mutableMapOf()

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

        btnCancel.setOnClickListener (View.OnClickListener {
            val intent = Intent(this, PlantInfo::class.java)
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

            updateJsonData(
                plantId.toString().toInt(),
                inputJustWatered,
                inputNotify,
                inputCurrHeight,
                inputGoalHeight
            )

            val intent = Intent(this, PlantInfo::class.java)
            intent.putExtra("inputWater", inputJustWatered)
            intent.putExtra("inputNotify", inputNotify)
            intent.putExtra("inputCurrHeight", inputCurrHeight)
            intent.putExtra("inputGoalHeight", inputGoalHeight)
            startActivity(intent)
        })
    }

    fun isValidNum(input: String): Boolean {
        return try {
            val number = input.toInt()
            number >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun updateJsonData(plantId: Int, inputJustWatered: Int, inputNotify: Int, inputCurrHeight: Int, inputGoalHeight: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("inputJustWatered", inputJustWatered)
        editor.putInt("inputNotify", inputNotify)
        editor.putInt("inputCurrHeight", inputCurrHeight)
        editor.putInt("inputGoalHeight", inputGoalHeight)
        editor.apply()

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

                    val editor = sharedPreferences.edit()
                    editor.putInt("inputJustWatered_$plantId", inputJustWatered)
                    editor.putInt("inputNotify_$plantId", inputNotify)
                    editor.putInt("inputCurrHeight_$plantId", inputCurrHeight)
                    editor.putInt("inputGoalHeight_$plantId", inputGoalHeight)
                    editor.apply()

                    break
                }
            }
            val updatedJsonString = jsonArray.toString()
            writeJsonToFile(updatedJsonString)
            val editor = sharedPreferences.edit()
            editor.putString("PlantData", jsonArray.toString())
            editor.apply()
        }
    }

    private fun writeJsonToFile(jsonString: String) {
        //write the json data back to the file
        val outputStream: OutputStream = openFileOutput("plants.json", Context.MODE_PRIVATE)
        outputStream.write(jsonString.toByteArray())
        outputStream.close()
        Log.d("SaveToJson", "Updated JSON file")
    }

}