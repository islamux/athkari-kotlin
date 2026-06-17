package com.athkarix.app.viewmodel

import com.athkarix.app.ui.screens.search.SearchViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchViewModelTest {

    private val viewModel = SearchViewModel()

    @Test
    fun `empty query produces empty results`() {
        viewModel.search("")
        assertTrue(viewModel.results.value.isEmpty())
    }

    @Test
    fun `blank query produces empty results`() {
        viewModel.search("   ")
        assertTrue(viewModel.results.value.isEmpty())
    }

    @Test
    fun `nonMatching query produces empty results`() {
        viewModel.search("zzzthisdoesnotexistzzz")
        assertTrue(viewModel.results.value.isEmpty())
    }

    @Test
    fun `query updates query state`() {
        viewModel.search("الله")
        assertEquals("الله", viewModel.query.value)
    }

    @Test
    fun `matching query returns results`() {
        viewModel.search("الله")
        assertTrue(viewModel.results.value.isNotEmpty())
    }

    @Test
    fun `search returns results with valid category and item`() {
        viewModel.search("رحمة")
        val results = viewModel.results.value
        assertTrue(results.isNotEmpty())
        results.forEach { result ->
            assertTrue(result.category.isNotBlank())
            assertTrue(result.categoryKey.isNotBlank())
            assertTrue(result.item.duaText?.isNotEmpty() == true)
        }
    }

    @Test
    fun `subsequent search clears previous results`() {
        viewModel.search("الله")
        assertTrue(viewModel.results.value.isNotEmpty())
        viewModel.search("")
        assertTrue(viewModel.results.value.isEmpty())
    }
}
