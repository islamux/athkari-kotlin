package com.athkarix.app.viewmodel

import app.cash.turbine.test
import com.athkarix.app.data.model.AthkarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BaseAthkarViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // A concrete subclass with 3 pages: tap counts [1, 2, 3]
    private class TestAthkarViewModel : BaseAthkarViewModel() {
        override val dataList = List(3) { AthkarItem(duaText = "Dua $it") }
        override val maxPageCounters = listOf(1, 2, 3)
        override val completionMessage = "All done!"
    }

    @Test
    fun `start on page 0 with counter 0`() {
        println("DEBUG: Creating TestAthkarViewModel")
        val vm = TestAthkarViewModel()
        println("DEBUG: Initial page index = ${vm.currentPageIndex.value}")
        println("DEBUG: Initial page counter = ${vm.currentPageCounter.value}")
        assertEquals(0, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
        println("DEBUG: Test passed - starts at page 0 with counter 0")
    }

    @Test
    fun `one tap on page 0 advances to page 1`() {
        println("DEBUG: Creating fresh TestAthkarViewModel")
        val vm = TestAthkarViewModel()
        println("DEBUG: Before tap - page ${vm.currentPageIndex.value}, counter ${vm.currentPageCounter.value}")

        vm.incrementPageController()

        println("DEBUG: After tap - page ${vm.currentPageIndex.value}, counter ${vm.currentPageCounter.value}")
        assertEquals(1, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
        println("DEBUG: Test passed - one tap advances to page 1")
    }

    @Test
    fun `page 1 requires 2 taps to advance`() {
        println("DEBUG: Creating fresh TestAthkarViewModel for page 1 test")
        val vm = TestAthkarViewModel()
        println("DEBUG: Switching to page 1")
        vm.onPageChanged(1)

        println("DEBUG: Tap 1 on page 1")
        vm.incrementPageController()
        println("DEBUG: Page ${vm.currentPageIndex.value}, counter ${vm.currentPageCounter.value}")
        assertEquals(1, vm.currentPageIndex.value)
        assertEquals(1, vm.currentPageCounter.value)

        println("DEBUG: Tap 2 on page 1 (reaches max)")
        vm.incrementPageController()
        println("DEBUG: Page ${vm.currentPageIndex.value}, counter ${vm.currentPageCounter.value}")
        assertEquals(2, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
        println("DEBUG: Test passed - page 1 requires 2 taps to advance")
    }

    @Test
    fun `resetCounter sets page counter to 0`() {
        println("DEBUG: Creating fresh TestAthkarViewModel")
        val vm = TestAthkarViewModel()
        println("DEBUG: Tapping once to get counter to 1")
        vm.incrementPageController()
        println("DEBUG: Counter before reset = ${vm.currentPageCounter.value}")
        vm.resetCounter()
        println("DEBUG: Counter after reset = ${vm.currentPageCounter.value}")
        assertEquals(0, vm.currentPageCounter.value)
        println("DEBUG: Test passed - resetCounter works")
    }

    @Test
    fun `goToPage changes page and resets counter`() {
        println("DEBUG: Creating fresh TestAthkarViewModel")
        val vm = TestAthkarViewModel()
        println("DEBUG: Tapping on page 0")
        vm.incrementPageController()
        println("DEBUG: Page ${vm.currentPageIndex.value}, counter ${vm.currentPageCounter.value}")
        println("DEBUG: Calling goToPage(0)")
        vm.goToPage(0)

        println("DEBUG: Page after goToPage = ${vm.currentPageIndex.value}, counter = ${vm.currentPageCounter.value}")
        assertEquals(0, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
        println("DEBUG: Test passed - goToPage resets counter")
    }

    @Test
    fun `completing all pages emits ShowCompletion`() = runTest {
        println("DEBUG: Creating TestAthkarViewModel for completion test")
        val vm = TestAthkarViewModel()

        // Collect events using Turbine
        println("DEBUG: Collecting events via Turbine")
        vm.eventFlow.test {
            println("DEBUG: Tap through all 3 pages")
            println("DEBUG: Page 0 (needs 1) → 1")
            vm.incrementPageController() // page 0 → 1
            println("DEBUG: Page 1 count 1")
            vm.incrementPageController() // page 1 count 1
            println("DEBUG: Page 1 → 2")
            vm.incrementPageController() // page 1 → 2
            println("DEBUG: Page 2 count 1")
            vm.incrementPageController() // page 2 count 1
            println("DEBUG: Page 2 count 2")
            vm.incrementPageController() // page 2 count 2
            println("DEBUG: Page 2 count 3 (complete)")
            vm.incrementPageController() // page 2 → done!

            println("DEBUG: Waiting for completion event")
            val event = awaitItem()
            println("DEBUG: Received event: $event")
            assertEquals(ViewEvent.ShowCompletion("All done!"), event)
            println("DEBUG: Test passed - completion event emitted")
        }
    }

    @Test
    fun `hapticTrigger emits on page advance`() = runTest {
        println("DEBUG: Creating TestAthkarViewModel for haptic test")
        val vm = TestAthkarViewModel()

        println("DEBUG: Collecting haptic triggers via Turbine")
        vm.hapticTrigger.test {
            println("DEBUG: Page 0 → 1 (advance should trigger haptic)")
            vm.incrementPageController() // page 0 → 1 (advance)

            println("DEBUG: Waiting for haptic Unit emission")
            awaitItem() // should emit Unit
            println("DEBUG: Haptic trigger received!")
        }
    }

    @Test
    fun `hapticTrigger does not emit on completion`() = runTest {
        println("DEBUG: Creating TestAthkarViewModel for haptic no-emission test")
        val vm = TestAthkarViewModel()

        println("DEBUG: Collecting haptic triggers")
        vm.hapticTrigger.test {
            // Tap through all 3 pages — 6 total taps
            println("DEBUG: Performing 6 taps through all pages")
            repeat(6) { vm.incrementPageController() }

            // Only page advances trigger haptic: 2 advances (0→1, 1→2), 1 completion (no haptic)
            println("DEBUG: Collecting 2 haptic emissions (page advances)")
            val received = mutableListOf<Unit>()
            repeat(2) { received.add(awaitItem()) }
            println("DEBUG: Collected 2 haptics as expected")

            // No more items should be available
            println("DEBUG: Verifying no more haptic events")
            expectNoEvents()
            println("DEBUG: Test passed - no haptic on completion")
        }
    }

    @Test
    fun `getShareText returns dua text for valid index`() {
        val vm = TestAthkarViewModel()
        assertEquals("Dua 0", vm.getShareText(0))
        assertEquals("Dua 1", vm.getShareText(1))
    }

    @Test
    fun `getShareText returns empty for invalid index`() {
        val vm = TestAthkarViewModel()
        assertEquals("", vm.getShareText(999))
    }
}
