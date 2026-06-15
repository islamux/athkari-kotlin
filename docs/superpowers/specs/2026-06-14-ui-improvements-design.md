# Athkarix UI Improvements — Design Spec

**Date:** 2026-06-14
**Source:** `test-my-ui.md`

---

## Summary

Five UI fixes to the Athkarix Android app: Arabic app name, consistent background, constrained home buttons, unified dhikr counter, and reset button for counter screens.

---

## 1. App Name → Arabic "أذكاري"

**File:** `HomeScreen.kt:136`

Change TopAppBar title from `"Athkarix"` to `"أذكاري"` using the existing string resource `R.string.app_name`.

```kotlin
title = { Text(stringResource(R.string.app_name)) }
```

**Scope:** Single line change.

---

## 2. Background Image on All Pages

**Problem:** Background image is buried under opaque scaffold surface on `AthkarScreen` and missing entirely on `SearchResultScreen`.

**Changes:**

### 2a. Swap background image
`BackgroundImage.kt` — change image resource from `R.drawable.bg_home` to `R.drawable.bg_91k`.

### 2b. Fix AthkarScreen layering
`AthkarScreen.kt` — set `Scaffold(containerColor = Color.Transparent)` so the `BackgroundImage` placed behind it shows through.

`AthkarTextSlider.kt` — reduce the full-box `Color.Black.copy(alpha = 0.4f)` overlay to `alpha = 0.2f` (or restructure to a gradient) to let more of the background show.

### 2c. Add BackgroundImage to SearchResultScreen
`SearchResultScreen.kt` — wrap existing `Scaffold` in a `Box` with `BackgroundImage(scrimAlpha = 0.6f)`, matching other screens.

### 2d. Fix NotificationSettingsScreen layering
`NotificationSettingsScreen.kt` — set `Scaffold(containerColor = Color.Transparent)`.

**Affected screens and treatment:**

| Screen | Treatment |
|---|---|
| HomeScreen | `BackgroundImage()` no scrim (unchanged) |
| AthkarScreen | `BackgroundImage(0.6)` + transparent Scaffold |
| SearchScreen | `BackgroundImage(0.6)` + Column overlay (unchanged) |
| SearchResultScreen | Add `BackgroundImage(0.6)` |
| NotificationSettings | `BackgroundImage(0.6)` + transparent Scaffold |

---

## 3. HomeScreen: 8 Buttons Visible, Scroll for Rest

**File:** `HomeScreen.kt`

**Change:**
- Remove `contentAlignment = Alignment.Center` so buttons start from top
- Constrain `LazyColumn` height to show hint text + exactly 8 buttons
- Remaining 3 buttons accessible via scroll

**Approximate height:** 8dp top spacer + 24dp hint text + 8 × 64dp (56dp button + 8dp spacing) ≈ 544dp

---

## 4. Sabah/Messa: Unified Counter (FAB mirrors page counter)

**Problem:** Two independent counters — `BaseAthkarViewModel.currentPageCounter` (page taps) and `FloatingCounterViewModel` (FAB). User wants the FAB to show the same value as the page counter.

**Changes:**

### 4a. Wire FAB to page counter
`AthkarScreen.kt` — show `viewModel.currentPageCounter` in `FloatingCounterFab` instead of `floatingCounterVM.counters[screenKey]`. FAB click calls `viewModel.incrementPageController()`.

### 4b. Reset support in BaseAthkarViewModel
Add `resetPageController()` method that resets `currentPageCounter` to 0 and returns to page 0.

---

## 5. Reset Button on 6 Screens

**Screens:** tasbih, estigfar, hamd, salatalalnbi, sabah, massa (all `showFloatingCounter = true`)

**UI:**
- Small `IconButton` with reset icon (`Icons.Default.Refresh`) at bottom-left corner of screen
- Opposite side from the FAB (which is bottom-right)
- Gold color, matching theme

**Behavior:**
- Resets current page counter to 0
- Resets floating counter to 0
- Returns to first page (page 0)

---

## Files Changed

| File | Change |
|---|---|
| `BackgroundImage.kt` | Swap to `bg_91k` |
| `HomeScreen.kt` | Arabic name; constrain to 8 buttons |
| `AthkarScreen.kt` | Transparent Scaffold; wire FAB to page counter; add reset button |
| `AthkarTextSlider.kt` | Reduce black overlay alpha |
| `SearchResultScreen.kt` | Add BackgroundImage |
| `NotificationSettingsScreen.kt` | Transparent Scaffold |
| `BaseAthkarViewModel.kt` | Add `resetPageController()` |

---

## Acceptance Criteria

1. Home TopAppBar shows "أذكاري"
2. Same background image visible on all screens (consistent opacity treatment)
3. Home shows hint + 8 buttons, scroll for remaining 3
4. FAB on sabah/massa shows current page tap count; FAB click advances page
5. Reset button visible at bottom-left on 6 counter screens; resets state to 0
