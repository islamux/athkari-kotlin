package com.athkarix.app.data.model

import org.json.JSONObject

data class AssmaHussnaItem(
    val id: Int,
    val name: String,
    val text: String,
) {
    companion object {
        fun fromJson(json: JSONObject): AssmaHussnaItem = AssmaHussnaItem(
            id = json.getInt("id"),
            name = json.getString("name"),
            text = json.getString("text"),
        )
    }
}
