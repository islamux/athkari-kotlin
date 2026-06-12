package com.athkarix.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DiacriticUtilTest{

  @Test
  fun `remove strips all tashkeel from Arabic text`(){
    println("DEBUG: Testing diacritic removal")
    val withDiacritics = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    val expected = "بسم الله الرحمن الرحيم"
    val result = DiacriticUtil.remove(withDiacritics)
    println("DEBUG: Input: $withDiacritics")
    println("DEBUG: Expected: $expected")
    println("DEBUG: Result: $result")
    assertEquals(expected, result)
    println("DEBUG: Test passed")
  }

    @Test
    fun `remove returns empty string for empty input`() {
        println("DEBUG: Testing empty input")
        val input = ""
        val result = DiacriticUtil.remove(input)
        println("DEBUG: Input: '$input'")
        println("DEBUG: Result: '$result'")
        assertEquals("", result)
        println("DEBUG: Test passed")
    }

    @Test
    fun `remove leaves plain text unchanged`() {
        println("DEBUG: Testing plain text unchanged")
        val plain = "الحمد لله"
        val result = DiacriticUtil.remove(plain)
        println("DEBUG: Input: $plain")
        println("DEBUG: Result: $result")
        assertEquals(plain, result)
        println("DEBUG: Test passed")
    }

    @Test
    fun `remove handles text with no Arabic characters`() {
        println("DEBUG: Testing non-Arabic text")
        val input = "Hello 123!"
        val result = DiacriticUtil.remove(input)
        println("DEBUG: Input: $input")
        println("DEBUG: Result: $result")
        assertEquals("Hello 123!", result)
        println("DEBUG: Test passed")
    }
}

