package com.athkarix.app.util

/** Strips Arabic diacritics (tashkeel) so search queries match regardless of vowel marks. */
object DiacriticUtil {
    private val diacritics = Regex("[\u064B-\u065F\u0670]")

    fun remove(text: String): String = text.replace(diacritics, "")
}
