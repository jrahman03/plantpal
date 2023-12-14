package edu.uw.ischool.c1ndyy.plantpal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.widget.Button

class ExplorePlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploreplant)

        val exploreCommonNames = intent.getStringArrayListExtra("commonNames")

        // Use the data
        val textView = findViewById<TextView>(R.id.explorePlant1)
        textView.text = exploreCommonNames.toString()

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
            val intent = Intent(this, ExplorePlant::class.java)
            startActivity(intent)
        }
    }
}