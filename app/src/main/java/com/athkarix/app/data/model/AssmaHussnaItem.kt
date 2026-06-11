package com.athkarix.app.data.model

import org.json.JSONObject

/** One of the 99 Names of Allah, loaded from assets JSON. */
data class AssmaHussnaItem(
    val id: Int,
    val name: String,
    val text: String,
) {
    /** Factory: parses a single JSON object into an AssmaHussnaItem. */
    companion object {
        fun fromJson(json: JSONObject): AssmaHussnaItem = AssmaHussnaItem(
            id = json.getInt("id"),
            name = json.getString("name"),
            text = json.getString("text"),
        )
    }
}
