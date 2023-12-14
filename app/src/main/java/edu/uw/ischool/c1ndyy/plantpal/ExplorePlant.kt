package edu.uw.ischool.c1ndyy.plantpal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent

class ExplorePlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploreplant)

        val exploreCommonNames = intent.getStringArrayListExtra("commonNames")

        // Use the data
        val textView = findViewById<TextView>(R.id.explorePlant1)
        textView.text = exploreCommonNames.toString()
    }
}