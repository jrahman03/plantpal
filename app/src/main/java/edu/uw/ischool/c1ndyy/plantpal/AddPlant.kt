package edu.uw.ischool.c1ndyy.plantpal
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
class AddPlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant) // Replace with your actual layout resource ID

        val buttonAddPlant = findViewById<Button>(R.id.buttonAddPlant)
        buttonAddPlant.setOnClickListener {
            // Handle the click event for adding a plant
        }
    }
}
