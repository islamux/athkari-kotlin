# Unit Testing Implementation Guide

> Builds on concepts from `09-unit-testing-guide.md` — read that first if you haven't.

This guide walks you through adding unit tests to Athkarix step by step. Each lesson teaches one new concept by testing real code from the app.

---

## Setup: Add Test Dependencies

Open `app/build.gradle.kts` and add inside `dependencies { }`:

```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.13.9")
testImplementation("app.cash.turbine:turbine:1.0.0")
```

Create the test directory:

```bash
mkdir -p app/src/test/java/com/athkarix/app
```

---

## Lesson 1: Pure Function Tests

**Concept:** Arrange → Act → Assert. A pure function has no dependencies — you call it, you check the result.

**Target:** `DiacriticUtil.remove()` — strips Arabic diacritics (tashkeel) from text.

**Test file:** `app/src/test/java/com/athkarix/app/util/DiacriticUtilTest.kt`

```kotlin
package com.athkarix.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DiacriticUtilTest {

    @Test
    fun `remove strips all tashkeel from Arabic text`() {
        val withDiacritics = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        val expected = "بسم الله الرحمن الرحيم"

        val result = DiacriticUtil.remove(withDiacritics)

        assertEquals(expected, result)
    }

    @Test
    fun `remove returns empty string for empty input`() {
        assertEquals("", DiacriticUtil.remove(""))
    }

    @Test
    fun `remove leaves plain text unchanged`() {
        val plain = "الحمد لله"
        assertEquals(plain, DiacriticUtil.remove(plain))
    }

    @Test
    fun `remove handles text with no Arabic characters`() {
        assertEquals("Hello 123!", DiacriticUtil.remove("Hello 123!"))
    }
}
```

**Key points:**
- `@Test` marks a function as a test.
- `assertEquals(expected, actual)` — order matters! Expected first.
- Test **edge cases**: empty string, already-clean text, non-Arabic text.
- Function name in backticks reads like a sentence — Kotlin convention.

**Run it:**
```bash
./gradlew testDebugUnitTest --tests "*DiacriticUtilTest*"
```

---

## Lesson 2: Simple ViewModel Tests

**Concept:** ViewModels expose `StateFlow`. Tests read `.value` to verify state changed.

### 2a: FloatingCounterViewModel

**Target:** Wrap-around counter (0 → 1 → ... → 99 → 0).

**Test file:** `app/src/test/java/com/athkarix/app/viewmodel/FloatingCounterViewModelTest.kt`

```kotlin
package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingCounterViewModelTest {

    private val viewModel = FloatingCounterViewModel()

    @Test
    fun `increment increases counter by 1`() {
        viewModel.increment()
        assertEquals(1, viewModel.counter.value)
    }

    @Test
    fun `incrementUntil100 wraps from 99 to 0`() {
        // Arrange: push counter to 99
        repeat(100) { viewModel.increment() }
        assertEquals(100, viewModel.counter.value)

        // Act: incrementUntil100 should wrap
        viewModel.incrementUntil100()
        assertEquals(0, viewModel.counter.value)
    }

    @Test
    fun `incrementUntil100 does not wrap below 100`() {
        repeat(99) { viewModel.increment() }
        assertEquals(99, viewModel.counter.value)

        viewModel.incrementUntil100()
        assertEquals(0, viewModel.counter.value)
    }

    @Test
    fun `reset sets counter to 0`() {
        viewModel.increment()
        viewModel.increment()
        viewModel.reset()
        assertEquals(0, viewModel.counter.value)
    }
}
```

### 2b: FontViewModel

**Target:** Font size clamped to 21–37 range, font family switching.

**Test file:** `app/src/test/java/com/athkarix/app/viewmodel/FontViewModelTest.kt`

```kotlin
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
    fun `changeFont updates selected font`() {
        viewModel.changeFont("sans-serif")
        assertEquals("sans-serif", viewModel.selectedFont.value)
    }
}
```

**Run both:**
```bash
./gradlew testDebugUnitTest --tests "*FloatingCounterViewModelTest*" --tests "*FontViewModelTest*"
```

---

## Lesson 3: Coroutine ViewModel Tests

**Concept:** Some ViewModels launch coroutines (`viewModelScope`). To test them, we need `runTest` from `kotlinx-coroutines-test` and Turbine for `SharedFlow`.

**Target:** `BaseAthkarViewModel.incrementPageController()` — the core page-advance logic.

### The Logic We're Testing

`incrementPageController()` does:
1. Increment the current page counter.
2. If counter reaches max (default 1 per page) → reset counter, advance page (or emit completion if last page).
3. If page advances → emit haptic trigger.
4. If all pages done → emit `ShowCompletion` event.

### Making It Testable

`BaseAthkarViewModel` is abstract. We need a concrete subclass for testing. The test uses `StandardTestDispatcher` to control coroutine timing.

**Test file:** `app/src/test/java/com/athkarix/app/viewmodel/BaseAthkarViewModelTest.kt`

```kotlin
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

    // A concrete subclass with 3 pages (default max = 1 tap per page)
    private class TestAthkarViewModel : BaseAthkarViewModel() {
        override val dataList = List(3) { AthkarItem(duaText = "Dua $it") }
        override val completionMessage = "All done!"
    }

    // Subclass with empty data list
    private class EmptyAthkarViewModel : BaseAthkarViewModel() {
        override val dataList = emptyList<AthkarItem>()
        override val completionMessage = "لا توجد أذكار"
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
    fun `multiple taps followed by sudden scroll should switch page and reset counter to 0`() {
        val vm = TestAthkarViewModel()
        vm.incrementPageController()
        vm.incrementPageController()
        vm.goToPage(1)

        assertEquals(1, vm.currentPageIndex.value)
        assertEquals(0, vm.currentPageCounter.value)
    }

    @Test
    fun `incrementPageController on empty list should not crash`() {
        val vm = EmptyAthkarViewModel()
        vm.incrementPageController()

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
            repeat(3) { vm.incrementPageController() }
            val received = mutableListOf<Unit>()
            repeat(2) { received.add(awaitItem()) }
            expectNoEvents()
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
```

**Key points:**
- `runTest { }` creates a coroutine scope for testing. Without it, `viewModelScope` calls crash.
- `vm.eventFlow.test { ... }` from Turbine lets you `awaitItem()` — it suspends until a new event is emitted.
- `expectNoEvents()` asserts no unexpected events leaked.
- `Dispatchers.setMain(StandardTestDispatcher())` in `@Before` ensures coroutines run predictably.
- Test edge cases: empty data list should not crash; scrolling mid-tap resets counter.

---

## Lesson 4: Mocking with MockK

**Concept:** `NotificationSettingsViewModel` depends on `SharedPrefsManager` and `NotificationService`. We don't want real Android prefs or real alarm scheduling in a test. We create **mocks** — fake objects that record what methods were called.

**Target:** `NotificationSettingsViewModel` — toggle morning/evening, update times.

**Test file:** `app/src/test/java/com/athkarix/app/viewmodel/NotificationSettingsViewModelTest.kt`

```kotlin
package com.athkarix.app.viewmodel

import com.athkarix.app.data.local.SharedPrefsManager
import com.athkarix.app.data.service.NotificationService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationSettingsViewModelTest {

    private val prefs: SharedPrefsManager = mockk()
    private val notificationService: NotificationService = mockk()

    @Test
    fun `initial state reads from SharedPrefs`() {
        // Arrange: tell mocks what to return
        every { prefs.morningEnabled } returns true
        every { prefs.eveningEnabled } returns false
        every { prefs.morningHour } returns 8
        every { prefs.morningMinute } returns 0
        every { prefs.eveningHour } returns 17
        every { prefs.eveningMinute } returns 30

        val vm = NotificationSettingsViewModel(prefs, notificationService)

        assertTrue(vm.morningEnabled.value)
        assertFalse(vm.eveningEnabled.value)
        assertEquals(8, vm.morningHour.value)
        assertEquals(0, vm.morningMinute.value)
        assertEquals(17, vm.eveningHour.value)
        assertEquals(30, vm.eveningMinute.value)
    }

    @Test
    fun `setMorningEnabled true schedules morning and persists`() {
        every { prefs.morningEnabled } returns false
        every { prefs.morningHour } returns 8
        every { prefs.morningMinute } returns 0
        every { prefs.morningEnabled = true } just runs
        every { notificationService.scheduleMorning(8, 0) } just runs

        val vm = NotificationSettingsViewModel(prefs, notificationService)

        vm.setMorningEnabled(true)

        assertTrue(vm.morningEnabled.value)
        verify { prefs.morningEnabled = true }
        verify { notificationService.scheduleMorning(8, 0) }
    }

    @Test
    fun `setMorningEnabled false cancels morning and persists`() {
        every { prefs.morningEnabled } returns true
        every { prefs.morningHour } returns 8
        every { prefs.morningMinute } returns 0
        every { prefs.morningEnabled = false } just runs
        every { notificationService.cancelMorning() } just runs

        val vm = NotificationSettingsViewModel(prefs, notificationService)

        vm.setMorningEnabled(false)

        assertFalse(vm.morningEnabled.value)
        verify { prefs.morningEnabled = false }
        verify { notificationService.cancelMorning() }
    }

    @Test
    fun `setMorningTime updates state and re-schedules if enabled`() {
        every { prefs.morningEnabled } returns true
        every { prefs.morningHour } returns 8
        every { prefs.morningMinute } returns 0
        every { prefs.eveningEnabled } returns false
        every { prefs.eveningHour } returns 17
        every { prefs.eveningMinute } returns 0
        every { prefs.morningHour = 9 } just runs
        every { prefs.morningMinute = 30 } just runs
        every { notificationService.cancelMorning() } just runs
        every { notificationService.scheduleMorning(9, 30) } just runs

        val vm = NotificationSettingsViewModel(prefs, notificationService)

        vm.setMorningTime(9, 30)

        assertEquals(9, vm.morningHour.value)
        assertEquals(30, vm.morningMinute.value)
        verify { prefs.morningHour = 9 }
        verify { prefs.morningMinute = 30 }
        verify { notificationService.cancelMorning() }
        verify { notificationService.scheduleMorning(9, 30) }
    }

    @Test
    fun `setMorningTime does not re-schedule if disabled`() {
        every { prefs.morningEnabled } returns false
        every { prefs.morningHour } returns 8
        every { prefs.morningMinute } returns 0
        every { prefs.eveningEnabled } returns false
        every { prefs.eveningHour } returns 17
        every { prefs.eveningMinute } returns 0
        every { prefs.morningHour = 10 } just runs
        every { prefs.morningMinute = 0 } just runs

        val vm = NotificationSettingsViewModel(prefs, notificationService)

        vm.setMorningTime(10, 0)

        // Should update prefs but NOT call notificationService
        verify { prefs.morningHour = 10 }
        verify { prefs.morningMinute = 0 }
        verify(exactly = 0) { notificationService.cancelMorning() }
        verify(exactly = 0) { notificationService.scheduleMorning(any(), any()) }
    }
}
```

**Key points:**
- `mockk<T>()` creates a mock — it's a "blank" object where every method returns nothing.
- `every { mock.method() } returns value` — "when this method is called, return this value".
- `verify { mock.method() }` — "prove this method was called".
- `verify(exactly = 0) { ... }` — "prove this method was NOT called".
- `just runs` — for void/Unit methods, tells MockK the call should succeed.
- Notice we never construct `SharedPrefsManager` or `NotificationService` for real — zero Android dependencies.

---

## Running All Tests

```bash
# All tests, all lessons
./gradlew testDebugUnitTest

# Single test class
./gradlew testDebugUnitTest --tests "*DiacriticUtilTest*"

# Filter by test name
./gradlew testDebugUnitTest --tests "*FontViewModelTest*"
```

Expected output:
```
BUILD SUCCESSFUL in Xs
```

---

## What We've Learned

| Lesson | Skill | Files Tested |
|--------|-------|-------------|
| 1 | Arrange → Act → Assert, edge cases | `DiacriticUtil` |
| 2 | StateFlow state verification | `FloatingCounterViewModel`, `FontViewModel` |
| 3 | Coroutines + Turbine for SharedFlow | `BaseAthkarViewModel` |
| 4 | Mocking with MockK, verify interactions | `NotificationSettingsViewModel` |

## What NOT to Test (Locally)

These need emulator/device tests (`androidTest`):
- Composable functions (UI rendering)
- `NotificationService` (AlarmManager, PendingIntent)
- `ShareUtil` / `WhatsAppUtil` (Context, startActivity)
- `AssmaHussnaService.loadAssmaHussnaData()` (assets access)

## Checklist for Adding Tests to New Code

1. Can it run on JVM without Android? → `test/` directory
2. Does it have Android deps? → `androidTest/` or refactor to extract pure logic
3. Does it use `viewModelScope`? → use `runTest { }`
4. Does it depend on other classes? → mock them with MockK
5. Did I test edge cases? → empty, null, max, min, boundary values
