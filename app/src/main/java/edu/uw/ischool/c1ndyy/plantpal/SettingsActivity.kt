package edu.uw.ischool.c1ndyy.plantpal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)
        val buttonRefreshCatalogue = findViewById<Button>(R.id.buttonRefreshCatalogue)
        val buttonDeletePlant = findViewById<Button>(R.id.buttonDeletePlant)





        // Implement the onClickListeners for your buttons here
        // For example:
        buttonEditProfile.setOnClickListener {
            // Code to edit profile
        }

        buttonRefreshCatalogue.setOnClickListener {
            // Code to refresh the plant catalogue
        }

        buttonDeletePlant.setOnClickListener {
            // Code to delete a plant
        }
    }
}
