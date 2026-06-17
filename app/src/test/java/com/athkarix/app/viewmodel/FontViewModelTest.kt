package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FontViewModelTest {

    private val viewModel = FontViewModel()

    @Test
    fun `default font size is 32`() {
        assertEquals(32.0f, viewModel.fontSize.value)
    }

    @Test
    fun `increaseFontSize adds 2`() {
        viewModel.increaseFontSize()
        assertEquals(34.0f, viewModel.fontSize.value)
    }

    @Test
    fun `decreaseFontSize subtracts 2`() {
        viewModel.increaseFontSize()
        viewModel.decreaseFontSize()
        assertEquals(32.0f, viewModel.fontSize.value)
    }

    @Test
    fun `font size cannot go below 24`() {
        repeat(10) { viewModel.decreaseFontSize() }
        assertEquals(24.0f, viewModel.fontSize.value)
    }

    @Test
    fun `font size cannot go above 42`() {
        repeat(10) { viewModel.increaseFontSize() }
        assertEquals(42.0f, viewModel.fontSize.value)
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
