package edu.uw.ischool.c1ndyy.plantpal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ArrayAdapter
import java.io.IOException
import org.json.JSONArray
import android.widget.ListView
import android.widget.LinearLayout



class ExplorePlant : AppCompatActivity() {
    private lateinit var plantsListView: ListView
    private lateinit var alphabetIndexLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploreplant)

        plantsListView = findViewById(R.id.plantsListView)
        alphabetIndexLayout = findViewById(R.id.alphabetIndexLayout)

        val latinNames = loadJsonData()?.let { parseJsonAndExtractLatinNames(it) }?.sorted() ?: listOf()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, latinNames)
        plantsListView.adapter = adapter


        setupAlphabetIndex(latinNames)
    }

    private fun loadJsonData(): String? {
        return try {
            assets.open("plants.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

    private fun parseJsonAndExtractLatinNames(jsonString: String): List<String> {
        val latinNames = mutableListOf<String>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val latin = jsonObject.getString("latin")
            latinNames.add(latin)
        }
        return latinNames
    }

    private fun setupAlphabetIndex(latinNames: List<String>) {
        val indexList = generateAlphabetIndex(latinNames)

        indexList.forEach { letter ->
            val letterView = TextView(this)
            letterView.text = letter.toString()
            letterView.setOnClickListener {
                val position = latinNames.indexOfFirst { it.startsWith(letter.toString(), ignoreCase = true) }
                if (position != -1) {
                    plantsListView.smoothScrollToPosition(position)
                }
            }
            alphabetIndexLayout.addView(letterView)
        }
    }

    private fun generateAlphabetIndex(latinNames: List<String>): Set<Char> {
        return latinNames.mapNotNull { it.firstOrNull()?.toUpperCase() }.toSortedSet()
    }
}