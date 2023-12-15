package edu.uw.ischool.c1ndyy.plantpal
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

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
            val intent = Intent(this, ExplorePlant::class.java)
            startActivity(intent)
        }

        // Implement the onClickListeners for your buttons here
        // For example:
        buttonEditProfile.setOnClickListener {
            // Check if SMS permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Request SMS permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, start the alarm
                startPlantUpdatesAlarm()
                Toast.makeText(this, "Text notifications regarding your plants will be sent 5 minutes", Toast.LENGTH_LONG).show()
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startPlantUpdatesAlarm()
                Toast.makeText(this, "Permission granted. Text notifications regarding your plants will be sent every 5 minutes", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "SMS permission denied. Cannot send text notifications.", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun startPlantUpdatesAlarm() {
        val alarmIntent = Intent(this, PlantUpdatesAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis = 5 * 60 * 1000

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis.toLong(),
            pendingIntent
        )
    }

}
