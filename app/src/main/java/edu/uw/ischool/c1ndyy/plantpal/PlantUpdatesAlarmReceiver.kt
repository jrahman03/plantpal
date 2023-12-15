package edu.uw.ischool.c1ndyy.plantpal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log

const val ALARM_ACTION = "ACTION_SEND_MESSAGE_ALARM"
const val SEND_SMS_PERMISSION_REQUEST_CODE = 1

class PlantUpdatesAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = "5554" // Replace with actual number
                val message = "Check on your Plants!"
                try {
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                    Log.d("PlantUpdatesAlarm", "SMS sent to $phoneNumber")
                } catch (e: Exception) {
                    Log.e("PlantUpdatesAlarm", "Failed to send SMS", e)
                }
            } else {
                Log.d("PlantUpdatesAlarm", "SMS Permission not granted")
            }
        }
    }
}