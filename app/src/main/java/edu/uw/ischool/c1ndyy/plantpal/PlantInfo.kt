package edu.uw.ischool.c1ndyy.plantpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class PlantInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantinfo)

        val btnEditPlant = findViewById<Button>(R.id.buttonEditPlant)
        val btnBack = findViewById<Button>(R.id.buttonBack)

        btnEditPlant.setOnClickListener (View.OnClickListener {
            val intent = Intent(this, EditPlant::class.java)
            //intent.putExtra("plant", position) need to pass in all the data
            startActivity(intent)
        })

        btnBack.setOnClickListener (View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }
}