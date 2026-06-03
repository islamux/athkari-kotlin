package com.athkarix.app.util

object DiacriticUtil {
    private val diacritics = Regex("[\u064B-\u065F\u0670]")

    fun remove(text: String): String = text.replace(diacritics, "")
}
