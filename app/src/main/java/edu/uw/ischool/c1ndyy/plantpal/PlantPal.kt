package edu.uw.ischool.c1ndyy.plantpal

import android.app.Application
import android.util.Log

class PlantPal : Application() {

    fun downloadAndUpdateData(url: String, callback: (String) -> Unit) {
        Log.d("PlantApp", "Fetching data from URL: $url")
        callback("Data fetched from $url")
    }
}