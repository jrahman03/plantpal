package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class AddPlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        val editTextPlantName = findViewById<EditText>(R.id.editTextPlantName)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val editTextType = findViewById<EditText>(R.id.editTextType)
        val editTextCareMethods = findViewById<EditText>(R.id.editTextCareMethods)
        val buttonAddPlant = findViewById<Button>(R.id.buttonAddPlant)

        buttonAddPlant.setOnClickListener {
            val plantName = editTextPlantName.text.toString()
            val description = editTextDescription.text.toString()
            val type = editTextType.text.toString()
            val careMethods = editTextCareMethods.text.toString()

            if (plantName.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty() && careMethods.isNotEmpty()) {
                val newPlant = JSONObject().apply {
                    put("name", plantName)
                    put("description", description)
                    put("type", type)
                    put("care_methods", careMethods)
                }

                // Now add this to the existing list and save it in SharedPreferences
                val sharedPreferences = getSharedPreferences("PlantPalData", MODE_PRIVATE)
                val plantsJsonString = sharedPreferences.getString("Plants", "[]")
                val plantsJsonArray = JSONArray(plantsJsonString)
                plantsJsonArray.put(newPlant)

                with(sharedPreferences.edit()) {
                    putString("Plants", plantsJsonArray.toString())
                    apply()
                }

                Toast.makeText(this, "Plant added successfully", Toast.LENGTH_SHORT).show()
                finish() // Close this activity and go back to the previous one
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

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
            val intent = Intent(this, AddPlant::class.java)
            startActivity(intent)
        }
    }
}
