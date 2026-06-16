package com.athkarix.app.viewmodel

import app.cash.turbine.test
import com.athkarix.app.data.model.AthkarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    // A concrete subclass with 3 pages, each needing 1 tap to advance
    private class TestAthkarViewModel : BaseAthkarViewModel() {
        override val dataList = List(3) { AthkarItem(duaText = "Dua $it") }
        override val completionMessage = "All done!"
    }

    // A concrete subclass : emptyList
    private class EmptyAthkarViewModel: BaseAthkarViewModel(){
       override val dataList = emptyList<AthkarItem>() // empty 
       override val completionMessage = "لا توجد أذكار"
    }
   
    @Test 
    fun `multiple taps folllowd by sudden scroll should switch page and reset counter 0`(){
      val viewmodel = TestAthkarViewModel()
      // 2 tabs 
      viewmodel.incrementPageController()
      viewmodel.incrementPageController()
      // scroll to page 1
      viewmodel.goToPage(1)

      // Read 
      assertEquals(1, viewmodel.currentPageIndex.value)
      assertEquals(0, viewmodel.currentPageCounter.value)
    }


    @Test 
    fun `incrementPageController on empty list should not crash`(){
      val viewmodel = EmptyAthkarViewModel()
      viewmodel.incrementPageController()

      assertEquals(0, viewmodel.currentPageIndex.value)
      assertEquals(0, viewmodel.currentPageCounter.value)

    }


    @Test
    fun `start on page 0 with counter 0`() {
        val vm = TestAthkarViewModel()
        assertEquals(0, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `one tap on page 0 advances to page 1`() {
        val vm = TestAthkarViewModel()
        vm.incrementPageController()
        assertEquals(1, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `tap advances to next page and resets counter`() {
        val vm = TestAthkarViewModel()
        assertEquals(0, vm.currentPageIndex.value)

        vm.incrementPageController()
        assertEquals(1, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)

        vm.incrementPageController()
        assertEquals(2, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `counter resets after each page advance`() {
        val vm = TestAthkarViewModel()
        assertEquals(0, vm.currentPageCounter.value)
        vm.incrementPageController()
        assertEquals(0, vm.currentPageCounter.value)
        vm.incrementPageController()
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `goToPage changes page and resets counter`() {
        val vm = TestAthkarViewModel()
        vm.incrementPageController()
        assertEquals(1, vm.currentPageIndex.value)
        vm.goToPage(0)
        assertEquals(0, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `completing all pages emits ShowCompletion`() = runTest {
        val vm = TestAthkarViewModel()
        vm.eventFlow.test {
            vm.incrementPageController() // page 0 → 1
            vm.incrementPageController() // page 1 → 2
            vm.incrementPageController() // page 2 → done!
            val event = awaitItem()
            assertEquals(ViewEvent.ShowCompletion("All done!"), event)
        }
    }

    @Test
    fun `hapticTrigger emits on page advance`() = runTest {
        val vm = TestAthkarViewModel()
        vm.hapticTrigger.test {
            vm.incrementPageController()
            awaitItem()
        }
    }

    @Test
    fun `hapticTrigger does not emit on completion`() = runTest {
        val vm = TestAthkarViewModel()
        vm.hapticTrigger.test {
            // 3 taps: 2 advances (haptic) + 1 completion (no haptic)
            repeat(3) { vm.incrementPageController() }
            val received = mutableListOf<Unit>()
            repeat(2) { received.add(awaitItem()) }
            expectNoEvents()
        }
    }

    @Test
    fun `tapping after completion emits haptic on every tap`() = runTest {
        val vm = TestAthkarViewModel()
        repeat(3) { vm.incrementPageController() }
        val receivedHaptics = mutableListOf<Unit>()
        val collector = launch { vm.hapticTrigger.collect { receivedHaptics.add(it) } }
        advanceUntilIdle()
        repeat(3) { vm.incrementPageController() }
        advanceUntilIdle()
        collector.cancel()
        assertEquals(3, receivedHaptics.size)
    }

    @Test
    fun `tapping after completion does not change page index or counter`() = runTest {
        val vm = TestAthkarViewModel()
        repeat(3) { vm.incrementPageController() }
        assertEquals(2, vm.currentPageIndex.value)
        val prevCounter = vm.currentPageCounter.value
        repeat(4) { vm.incrementPageController() }
        assertEquals(2, vm.currentPageIndex.value)
        assertEquals(prevCounter, vm.currentPageCounter.value)
    }

    @Test
    fun `resetPageController clears completed flag so navigation works again`() = runTest {
        val vm = TestAthkarViewModel()
        repeat(3) { vm.incrementPageController() }
        vm.resetPageController()
        assertEquals(0, vm.currentPageIndex.value)
        vm.incrementPageController()
        assertEquals(1, vm.currentPageIndex.value)
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
