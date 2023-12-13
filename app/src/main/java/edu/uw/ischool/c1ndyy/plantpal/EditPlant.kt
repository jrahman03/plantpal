package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.io.IOException


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

        val plantNameInfo = getIntent().getStringExtra("plantName")
        val plantTypeInfo = getIntent().getStringExtra("plantType")
        val careMethodInfo = getIntent().getStringExtra("careMethod")
        val lastWateredInfo = getIntent().getIntExtra("lastWatered", 0)
        val notifyInfo = getIntent().getIntExtra("notify", 0)
        val currHeightInfo = getIntent().getIntExtra("currHeight", 0)
        val goalHeightInfo = getIntent().getIntExtra("goalHeight", 0)

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
            startActivity(intent)
        })

        saveChanges.setOnClickListener (View.OnClickListener {
            val inputJustWatered = lastWatered.text.toString().toInt()
            val inputNotify = notifSetting.text.toString().toInt()
            val inputCurrHeight = currHeight.text.toString().toInt()
            val inputGoalHeight = heightGoal.text.toString().toInt()

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

}