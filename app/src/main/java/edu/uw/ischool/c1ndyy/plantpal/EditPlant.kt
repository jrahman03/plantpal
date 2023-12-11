package edu.uw.ischool.c1ndyy.plantpal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.io.IOException


class EditPlant : AppCompatActivity() {

    data class PlantData(var plantIndex: Int, var notifSettingNum: Int, var heightGoal: Int, var currHeight: Int)

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var notifSetting : EditText
    lateinit var currHeight : EditText
    lateinit var heightGoal : EditText
    lateinit var saveChanges : Button
    private lateinit var plantName: String
    private val plantDataMap: MutableMap<String, PlantData> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editplant)

        sharedPreferences = getSharedPreferences("PlantPalPreferences", MODE_PRIVATE)
        notifSetting = findViewById(R.id.inputNotifySetting)
        currHeight = findViewById(R.id.inputCurrHeight)
        heightGoal = findViewById(R.id.inputHeightGoal)
        saveChanges = findViewById(R.id.buttonSaveChanges)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // make sure values are valid
                val allFieldsFilled = !notifSetting.text.isNullOrBlank() &&
                        !currHeight.text.isNullOrBlank() &&
                        !heightGoal.text.isNullOrBlank() &&
                        isValidNum(notifSetting.text.toString()) &&
                        isValidNum(currHeight.text.toString()) &&
                        isValidNum(heightGoal.text.toString())

                if (allFieldsFilled) {
                    notifSetting.error = null
                    currHeight.error = null
                    heightGoal.error = null
                    saveChanges.isEnabled = true
                } else {
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

        notifSetting.addTextChangedListener(textWatcher)
        currHeight.addTextChangedListener(textWatcher)
        heightGoal.addTextChangedListener(textWatcher)


    }

    fun isValidNum(input: String): Boolean {
        return try {
            val number = input.toInt()
            number >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun saveData() {
        /*
        implement this part in where the user is clicking the individual plant itself to pass in the plant data
        val intent = Intent(this, EditPlant::class.java)
        intent.putExtra("plantData", selectedPlantJsonObject.toString())
        startActivity(intent)
         */

        val plantDataString = intent.getStringExtra("plantData")
        val plantJsonObject = JSONObject(plantDataString)


    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

}