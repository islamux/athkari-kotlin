# Plan: Add Missing Tests

> 2026-06-13

Two test gaps from `docs/juniors/10-unit-testing-implementation-guide.md`:

| Missing | File | Tests Missing |
|---------|------|---------------|
| 1 test | `FloatingCounterViewModelTest` | `incrementUntil100 does not wrap below 100` |
| Full file | `NotificationSettingsViewModelTest` | 5 tests (entire Lesson 4) |

---

## Task 1: Add `incrementUntil100 does not wrap below 100`

**File:** `app/src/test/java/com/athkarix/app/viewmodel/FloatingCounterViewModelTest.kt`

Add one test after `incrementUntil100 wraps from 99 to 0`:

```kotlin
@Test
fun `incrementUntil100 does not wrap below 100`() {
    repeat(99) { viewModel.increment() }
    assertEquals(99, viewModel.counter.value)

    viewModel.incrementUntil100()
    assertEquals(0, viewModel.counter.value)
}
```

**Verification:**
```bash
./gradlew test --tests "*FloatingCounterViewModelTest*"
```

---

## Task 2: Create `NotificationSettingsViewModelTest`

**New file:** `app/src/test/java/com/athkarix/app/viewmodel/NotificationSettingsViewModelTest.kt`

### Prerequisites
- MockK already in test dependencies ✅
- No Turbine needed (StateFlow only, no SharedFlow)

### Tests

| # | Test Name | Verifies |
|---|-----------|----------|
| 1 | `initial state reads from SharedPrefs` | Constructor reads all 6 prefs values |
| 2 | `setMorningEnabled true schedules and persists` | Calls `scheduleMorning()` + sets `prefs.morningEnabled = true` |
| 3 | `setMorningEnabled false cancels and persists` | Calls `cancelMorning()` + sets `prefs.morningEnabled = false` |
| 4 | `setMorningTime updates and re-schedules if enabled` | Calls `cancelMorning()` + `scheduleMorning(newHour, newMin)` |
| 5 | `setMorningTime does not re-schedule if disabled` | Updates prefs only, zero service calls |

### Full test class

```kotlin
package com.athkarix.app.viewmodel

import com.athkarix.app.data.local.SharedPrefsManager
import com.athkarix.app.data.service.NotificationService
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

        verify { prefs.morningHour = 10 }
        verify { prefs.morningMinute = 0 }
        verify(exactly = 0) { notificationService.cancelMorning() }
        verify(exactly = 0) { notificationService.scheduleMorning(any(), any()) }
    }
}
```

---

## Verification

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL` — all existing + 6 new tests passing.

---

## Out of Scope

- Debug `println()` cleanup in existing tests — not broken, just noisy
- Floating counter reset button — covered by existing `reset()` test
- UI tests (`androidTest`) — no infrastructure in project

---

## Tracker Update

Add to `project-tracker.json` backlog:
- `id`: `missing_tests_001` — add `incrementUntil100 does not wrap below 100`
- `id`: `missing_tests_002` — create `NotificationSettingsViewModelTest` with 5 MockK tests
