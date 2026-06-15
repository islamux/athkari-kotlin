package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FontViewModelTest {

    private val viewModel = FontViewModel()

    @Test
    fun `default font size is 28 point 6`() {
        assertEquals(28.6f, viewModel.fontSize.value)
    }

    @Test
    fun `increaseFontSize adds 2`() {
        viewModel.increaseFontSize()
        assertEquals(30.6f, viewModel.fontSize.value)
    }

    @Test
    fun `decreaseFontSize subtracts 2`() {
        viewModel.increaseFontSize()
        viewModel.decreaseFontSize()
        assertEquals(28.6f, viewModel.fontSize.value)
    }

    @Test
    fun `font size cannot go below 21`() {
        repeat(10) { viewModel.decreaseFontSize() }
        assertEquals(21.0f, viewModel.fontSize.value)
    }

    @Test
    fun `font size cannot go above 37`() {
        repeat(10) { viewModel.increaseFontSize() }
        assertEquals(37.0f, viewModel.fontSize.value)
    }

    @Test
    fun `toggleFont switches between Amiri and Cairo`() {
        assertEquals("Amiri", viewModel.selectedFont.value)
        viewModel.toggleFont()
        assertEquals("Cairo", viewModel.selectedFont.value)
        viewModel.toggleFont()
        assertEquals("Amiri", viewModel.selectedFont.value)
    }
}
