package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
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

        //nav bar
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




        // Implement the onClickListeners for your buttons here
        // For example:
        buttonEditProfile.setOnClickListener {
            // Code to edit profile
        }

        buttonRefreshCatalogue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("showPreferences", true)
            startActivity(intent)
        }

        buttonDeletePlant.setOnClickListener {
            // Code to delete a plant
        }
    }
}
