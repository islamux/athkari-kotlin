package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FontViewModelTest {

    private val viewModel = FontViewModel()

    @Test
    fun `default font size is 28 point 6`() {
        println("DEBUG: Starting test - initial font size = ${viewModel.fontSize.value}")
        assertEquals(28.6f, viewModel.fontSize.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `increaseFontSize adds 2`() {
        println("DEBUG: Increasing font size")
        viewModel.increaseFontSize()
        println("DEBUG: After increase font size = ${viewModel.fontSize.value}")
        assertEquals(30.6f, viewModel.fontSize.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `decreaseFontSize subtracts 2`() {
        viewModel.increaseFontSize()
        println("DEBUG: Increasing font size, now = ${viewModel.fontSize.value}")
        viewModel.decreaseFontSize()
        println("DEBUG: After decrease font size = ${viewModel.fontSize.value}")
        assertEquals(28.6f, viewModel.fontSize.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `font size cannot go below 21`() {
        println("DEBUG: Testing font size clamp - min 21")
        repeat(10) { viewModel.decreaseFontSize() }
        println("DEBUG: Final font size = ${viewModel.fontSize.value}")
        assertEquals(21.0f, viewModel.fontSize.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `font size cannot go above 37`() {
        println("DEBUG: Testing font size clamp - max 37")
        repeat(10) { viewModel.increaseFontSize() }
        println("DEBUG: Final font size = ${viewModel.fontSize.value}")
        assertEquals(37.0f, viewModel.fontSize.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `changeFont updates selected font`() {
        println("DEBUG: Starting test - initial font = ${viewModel.selectedFont.value}")
        viewModel.changeFont("sans-serif")
        println("DEBUG: Changed font to = ${viewModel.selectedFont.value}")
        assertEquals("sans-serif", viewModel.selectedFont.value)
        println("DEBUG: Test passed")
    }
}
