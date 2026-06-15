package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingCounterViewModelTest {

    private val viewModel = FloatingCounterViewModel()

    @Test
    fun `increment increases counter for specific key by 1`() {
        viewModel.increment("tasbih")
        assertEquals(1, viewModel.counters.value["tasbih"])
    }

    @Test
    fun `counters for different keys are independent`() {
        viewModel.increment("tasbih")
        viewModel.increment("tasbih")
        viewModel.increment("estigfar")
        assertEquals(2, viewModel.counters.value["tasbih"])
        assertEquals(1, viewModel.counters.value["estigfar"])
    }

    @Test
    fun `reset sets counter to 0 for specific key`() {
        viewModel.increment("tasbih")
        viewModel.increment("tasbih")
        viewModel.reset("tasbih")
        assertEquals(0, viewModel.counters.value["tasbih"])
    }

    @Test
    fun `reset does not affect other keys`() {
        viewModel.increment("tasbih")
        viewModel.increment("estigfar")
        viewModel.reset("tasbih")
        assertEquals(0, viewModel.counters.value["tasbih"] ?: 0)
        assertEquals(1, viewModel.counters.value["estigfar"] ?: 0)
    }
}
