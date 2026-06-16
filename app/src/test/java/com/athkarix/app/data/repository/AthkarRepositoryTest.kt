package com.athkarix.app.data.repository

import org.junit.Assert.assertTrue
import org.junit.Test

class AthkarRepositoryTest {

    @Test
    fun `sabah list is non-empty`() {
        assertTrue(AthkarRepository.athkarSabahList.isNotEmpty())
    }

    @Test
    fun `massa list is non-empty`() {
        assertTrue(AthkarRepository.athkarMassaList.isNotEmpty())
    }

    @Test
    fun `all sabah items have non-null duaText`() {
        AthkarRepository.athkarSabahList.forEachIndexed { index, item ->
            assertTrue("Sabah item $index has null duaText", item.duaText != null)
        }
    }

    @Test
    fun `all massa items have non-null duaText`() {
        AthkarRepository.athkarMassaList.forEachIndexed { index, item ->
            assertTrue("Massa item $index has null duaText", item.duaText != null)
        }
    }
}
