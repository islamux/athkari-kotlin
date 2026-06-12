# Component Extraction & Reorganization Design

**Date:** 2026-06-11
**Status:** Approved

## Goal

Extract inline composables from screen files into shared components in `ui/components/`, organized by concern into subdirectories. No behavior changes.

## Success Criteria

- Zero behavior changes — screens render identically
- All imports updated and compilation passes
- Each component has a single clear purpose

---

## Directory Structure After Refactor

```
ui/components/
├── common/
│   ├── CustomButton.kt              # existing — gold styled button
│   ├── AlertExitApp.kt              # existing — exit confirmation dialog
│   ├── LoadingErrorContent.kt       # NEW — loading/error/retry
│   ├── AthkarixTopAppBar.kt         # NEW — back button + title
│   ├── BackgroundImage.kt           # NEW — full-screen cropped bg
│   └── ExitGuard.kt                 # NEW — BackHandler + exit dialog
│
├── dua/
│   ├── AthkarTextSlider.kt          # existing — swipeable dua pager
│   ├── DuaContent.kt                # NEW — scrollable dua text + footer
│   └── FontControls.kt              # existing — font size +/- buttons
│
├── search/
│   └── SearchTextField.kt           # NEW — styled search input with clear
│
├── notification/
│   └── NotificationToggleRow.kt     # NEW — label + switch + time picker
│
└── navigation/
    ├── CustomDrawer.kt              # existing — app drawer
    └── FloatingCounterFab.kt        # existing — circular counter FAB
```

## New Components — Signatures & Source

### 1. `common/AthkarixTopAppBar.kt`

```kotlin
@Composable
fun AthkarixTopAppBar(
    title: String = "",
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
)
```

Extracted from: AssmaHussnaScreen.kt, SearchResultScreen.kt, NotificationSettingsScreen.kt, AthkarScreen.kt

### 2. `common/LoadingErrorContent.kt`

```kotlin
@Composable
fun LoadingErrorContent(
    isLoading: Boolean,
    hasError: Boolean,
    errorMessage: String,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
)
```

Extracted from: AssmaHussnaScreen.kt

### 3. `common/BackgroundImage.kt`

```kotlin
@Composable
fun BackgroundImage(
    painter: Painter,
    modifier: Modifier = Modifier,
)
```

Extracted from: HomeScreen.kt

### 4. `common/ExitGuard.kt`

```kotlin
@Composable
fun ExitGuard(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit,
)
```

Extracted from: HomeScreen.kt

### 5. `dua/DuaContent.kt`

```kotlin
@Composable
fun DuaContent(
    duaText: String?,
    footer: String?,
    fontFamily: String = "Amiri",
    fontSize: Float = 28.6f,
)
```

Extracted from: SearchResultScreen.kt (duplicates AthkarTextSlider page rendering)

### 6. `search/SearchTextField.kt`

```kotlin
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
)
```

Extracted from: SearchScreen.kt

### 7. `notification/NotificationToggleRow.kt`

```kotlin
@Composable
fun NotificationToggleRow(
    label: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    timeText: String,
    onTimeClick: () -> Unit,
)
```

Extracted from: NotificationSettingsScreen.kt

## Implementation Order

Each step is atomic: create file → update screen imports → delete inline code → verify.

1. **common/AthkarixTopAppBar** — extracted from 4 screens, biggest duplication win
2. **dua/DuaContent** — eliminate duplication between 2 files
3. **common/LoadingErrorContent** — from AssmaHussnaScreen
4. **common/BackgroundImage** — from HomeScreen
5. **common/ExitGuard** — from HomeScreen
6. **search/SearchTextField** — from SearchScreen
7. **notification/NotificationToggleRow** — from NotificationSettingsScreen
8. **Move existing components** — relocate 6 existing files into subdirectories, update all imports

## Files to Update

- HomeScreen.kt (remove inline: BackgroundImage, ExitGuard)
- AssmaHussnaScreen.kt (remove inline: LoadingErrorContent, AthkarixTopAppBar)
- AthkarScreen.kt (replace inline TopAppBar with AthkarixTopAppBar)
- SearchResultScreen.kt (replace inline TopAppBar + inline dua content)
- SearchScreen.kt (remove inline SearchTextField)
- NotificationSettingsScreen.kt (remove inline: NotificationToggleRow, AthkarixTopAppBar)
- AthkarixNavGraph.kt (import paths if needed)

## Risk Mitigation

- Each extraction is small and self-contained — if one breaks, others are unaffected
- No behavior changes — extracted code is identical, just relocated
- Verify with `./gradlew lint` after each batch
