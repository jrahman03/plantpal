package edu.uw.ischool.c1ndyy.plantpal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.util.Log
import android.widget.Button

class ExplorePlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploreplant)

        val exploreCommonNames = intent.getStringArrayListExtra("commonNames")

        // Use the data
        val textView1 = findViewById<TextView>(R.id.explorePlant1)
        val textView2 = findViewById<TextView>(R.id.explorePlant2)
        val textView3 = findViewById<TextView>(R.id.explorePlant3)
        val textView4 = findViewById<TextView>(R.id.explorePlant4)
        val textView5 = findViewById<TextView>(R.id.explorePlant5)
        val textView6 = findViewById<TextView>(R.id.explorePlant6)
        val textView7 = findViewById<TextView>(R.id.explorePlant7)
        val textView8 = findViewById<TextView>(R.id.explorePlant8)
        val textView9 = findViewById<TextView>(R.id.explorePlant9)
        val textView10 = findViewById<TextView>(R.id.explorePlant10)
        textView1.text = "(Load data (e.g. 'plants.json') in settings to explore plants)"

        if (exploreCommonNames != null) {
            // val testingName = exploreCommonNames[1].toString()
            // Log.d("name", testingName.toString())

            textView1.text = exploreCommonNames[0].toString()
            textView2.text = exploreCommonNames[1].toString()
            textView3.text = exploreCommonNames[2].toString()
            textView4.text = exploreCommonNames[3].toString()
            textView5.text = exploreCommonNames[4].toString()
            textView6.text = exploreCommonNames[5].toString()
            textView7.text = exploreCommonNames[6].toString()
            textView8.text = exploreCommonNames[7].toString()
            textView9.text = exploreCommonNames[8].toString()
            textView10.text = exploreCommonNames[9].toString()
        } else {
            textView1.text = "(Load data (e.g. 'plants.json') in settings to explore plants)"
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
            val intent = Intent(this, ExplorePlant::class.java)
            startActivity(intent)
        }
    }
}