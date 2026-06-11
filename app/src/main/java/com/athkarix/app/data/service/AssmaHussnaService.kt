package com.athkarix.app.data.service

import android.content.Context
import com.athkarix.app.data.model.AssmaHussnaItem
import org.json.JSONArray

/** Singleton service that loads, caches, and searches the 99 Names from assets JSON. */
object AssmaHussnaService {
    private const val JSON_FILE = "json/assma-hussna.json"
    private var cachedData: List<AssmaHussnaItem>? = null

    /** Loads from assets on first call; returns cached list afterwards. */
    fun loadAssmaHussnaData(context: Context): List<AssmaHussnaItem> {
        cachedData?.let { return it }

        val jsonString = context.assets.open(JSON_FILE)
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val items = mutableListOf<AssmaHussnaItem>()
        for (i in 0 until jsonArray.length()) {
            items.add(AssmaHussnaItem.fromJson(jsonArray.getJSONObject(i)))
        }

        cachedData = items
        return items
    }

    // — Query helpers —
    fun getAssmaHussnaById(context: Context, id: Int): AssmaHussnaItem? {
        return loadAssmaHussnaData(context).find { it.id == id }
    }

    fun getAllAssmaHussna(context: Context): List<AssmaHussnaItem> {
        return loadAssmaHussnaData(context)
    }

    fun searchByName(context: Context, query: String): List<AssmaHussnaItem> {
        return loadAssmaHussnaData(context).filter { it.name.contains(query) }
    }

    fun searchByText(context: Context, query: String): List<AssmaHussnaItem> {
        return loadAssmaHussnaData(context).filter { it.text.contains(query) }
    }

    fun getCount(context: Context): Int = loadAssmaHussnaData(context).size

    // — Cache management —
    fun clearCache() {
        cachedData = null
    }

    /** Sanity check: expects exactly 99 (or 100) names, unique IDs, non-empty fields. */
    fun validateData(context: Context): Boolean {
        return try {
            val data = loadAssmaHussnaData(context)
            if (data.size != 99 && data.size != 100) return false
            val ids = data.map { it.id }.toSet()
            if (ids.size != data.size) return false
            data.all { it.name.isNotEmpty() && it.text.isNotEmpty() }
        } catch (e: Exception) {
            false
        }
    }
}
