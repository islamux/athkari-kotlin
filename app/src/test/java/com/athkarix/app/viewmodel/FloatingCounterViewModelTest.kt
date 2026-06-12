package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingCounterViewModelTest {

    private val viewModel = FloatingCounterViewModel()

    @Test
    fun `increment increases counter by 1`() {
        println("DEBUG: Incrementing counter from ${viewModel.counter.value}")
        viewModel.increment()
        println("DEBUG: Counter after increment = ${viewModel.counter.value}")
        assertEquals(1, viewModel.counter.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `incrementUntil100 wraps from 99 to 0`() {
        println("DEBUG: Incrementing to 99")
        repeat(100) { viewModel.increment() }
        println("DEBUG: Counter at 99, now calling incrementUntil100()")
        viewModel.incrementUntil100()
        println("DEBUG: Counter after incrementUntil100 = ${viewModel.counter.value}")
        assertEquals(0, viewModel.counter.value)
        println("DEBUG: Test passed")
    }

    @Test
    fun `reset sets counter to 0`() {
        println("DEBUG: Incrementing twice to 2")
        viewModel.increment()
        viewModel.increment()
        println("DEBUG: Counter at 2, calling reset()")
        viewModel.reset()
        println("DEBUG: Counter after reset = ${viewModel.counter.value}")
        assertEquals(0, viewModel.counter.value)
        println("DEBUG: Test passed")
    }
}
