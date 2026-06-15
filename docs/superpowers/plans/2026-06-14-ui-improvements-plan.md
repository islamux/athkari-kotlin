# UI Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply 5 UI fixes: Arabic app name, consistent background across all screens, constrain home to 8 buttons, unify FAB counter with page counter, add reset button.

**Architecture:** Each fix is an isolated change targeting one or two files. The FAB counter change requires coordination between `AthkarScreen.kt` and `BaseAthkarViewModel.kt`. Background changes span 5 screen files but each is independent.

**Tech Stack:** Kotlin, Jetpack Compose, Material3

---

### Task 1: Swap background image

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/common/BackgroundImage.kt:21`

- [ ] **Step 1: Change image resource**

In `BackgroundImage.kt:21`, change `R.drawable.bg_home` to `R.drawable.bg_91k`:

```kotlin
painter = painterResource(R.drawable.bg_91k),
```

- [ ] **Step 2: Verify the resource exists**

```bash
ls app/src/main/res/drawable/bg_91k*
```
Expected: `bg_91k.jpg` listed.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/components/common/BackgroundImage.kt
git commit -m "feat: swap background image to bg_91k"
```

---

### Task 2: Add background to SearchResultScreen

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchResultScreen.kt`

- [ ] **Step 1: Wrap Scaffold in a Box with BackgroundImage**

Replace the current `SearchResultScreen` with one that wraps `Scaffold` in a `Box` with `BackgroundImage`:

```kotlin
package com.athkarix.app.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.dua.DuaContent

@Composable
fun SearchResultScreen(
    item: AthkarItem,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(scrimAlpha = 0.6f)
        Scaffold(
            topBar = {
                AthkarixTopAppBar(onBack = onBack)
            },
        ) { padding ->
            DuaContent(
                duaText = item.duaText,
                footer = item.footer,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
```

Key changes:
- Added imports for `Box`, `fillMaxSize`, `BackgroundImage`
- Wrapped `Scaffold` in `Box` with `BackgroundImage(scrimAlpha = 0.6f)`
- Removed `containerColor = Color.Black`
- Removed `import androidx.compose.ui.graphics.Color`

- [ ] **Step 2: Verify it compiles**

```bash
./gradlew lint || true
```

No new lint errors expected (pre-existing `NotificationService.kt` NewApi errors are unrelated).

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/search/SearchResultScreen.kt
git commit -m "fix: add background image to search result screen"
```

---

### Task 3: Reduce AthkarTextSlider overlay

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/dua/AthkarTextSlider.kt:79`

- [ ] **Step 1: Reduce black overlay alpha**

Change line 79 from `0.4f` to `0.2f`:

```kotlin
.background(Color.Black.copy(alpha = 0.2f))
```

This lets more of the background image show through the text area.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/components/dua/AthkarTextSlider.kt
git commit -m "fix: reduce text slider overlay alpha to 0.2"
```

---

### Task 4: Fix AthkarScreen background layering

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt:70`

- [ ] **Step 1: Make Scaffold background transparent**

In `AthkarScreen.kt`, add `containerColor = Color.Transparent` to the `Scaffold` call at line 70:

```kotlin
Scaffold(
    containerColor = Color.Transparent,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
```

Add the import for `Color` if not already present.

- [ ] **Step 2: Verify it compiles**

```bash
./gradlew lint || true
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt
git commit -m "fix: make scaffold transparent so background shows through"
```

---

### Task 5: Fix NotificationSettingsScreen background

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/settings/NotificationSettingsScreen.kt:35`

- [ ] **Step 1: Make Scaffold transparent**

In `NotificationSettingsScreen.kt`, add `containerColor = Color.Transparent` to the `Scaffold`:

```kotlin
Scaffold(
    containerColor = Color.Transparent,
    topBar = {
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/settings/NotificationSettingsScreen.kt
git commit -m "fix: make notification settings scaffold transparent"
```

---

### Task 6: Change app name to Arabic

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt:136`

- [ ] **Step 1: Change TopAppBar title to use string resource**

In `HomeScreen.kt`, replace:

```kotlin
title = { Text("Athkarix") },
```

with:

```kotlin
title = { Text(stringResource(R.string.app_name)) },
```

Add the import for `stringResource` if not present (it's in `androidx.compose.ui.res`).

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt
git commit -m "feat: use Arabic app name from string resource"
```

---

### Task 7: Constrain HomeScreen to 8 buttons + hint

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt`

- [ ] **Step 1: Remove centered alignment and constrain height**

Replace the content Box:

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
```

with top-aligned + height-constrained:

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 544.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
```

This removes `contentAlignment = Alignment.Center` and adds `Modifier.heightIn(max = 544.dp)` to the LazyColumn. The hint + 8 buttons (~544dp total) will be visible, remaining 3 scrollable.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt
git commit -m "fix: constrain home to 8 visible buttons with scroll for rest"
```

---

### Task 8: Add resetPageController to BaseAthkarViewModel

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/viewmodel/BaseAthkarViewModel.kt`

- [ ] **Step 1: Add resetPageController method**

After the existing `resetCounter()` method (line 44), add:

```kotlin
fun resetPageController() {
    _currentPageIndex.value = 0
    _currentPageCounter.value = 0
}
```

This resets both the page index and counter to 0.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/athkarix/app/viewmodel/BaseAthkarViewModel.kt
git commit -m "feat: add resetPageController to base viewmodel"
```

---

### Task 9: Wire FAB to page counter and add reset button

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt`

- [ ] **Step 1: Replace FAB binding and add reset button**

Replace the current FAB + imports in `AthkarScreen.kt`:

Current FAB section (lines 85-91):
```kotlin
floatingActionButton = {
    if (showFloatingCounter && floatingCounterVM != null) {
        val counters by floatingCounterVM.counters.collectAsState()
        val count = counters[screenKey] ?: 0
        FloatingCounterFab(counter = count, onClick = { floatingCounterVM.increment(screenKey) })
    }
}
```

Replace with:
```kotlin
floatingActionButton = {
    if (showFloatingCounter && floatingCounterVM != null) {
        FloatingCounterFab(counter = pageCounter, onClick = { viewModel.incrementPageController() })
    }
}
```

Add reset button after the Scaffold closing brace but inside the outer Box. The full AthkarScreen should look like:

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    BackgroundImage(scrimAlpha = 0.6f)
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AthkarixTopAppBar(
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        onShare(viewModel.getShareText(viewModel.currentPageIndex.value))
                    }) {
                        Icon(Icons.Default.Share, "مشاركة", tint = AppColor.primaryGold)
                    }
                    FontControls(fontViewModel)
                },
            )
        },
        floatingActionButton = {
            if (showFloatingCounter && floatingCounterVM != null) {
                FloatingCounterFab(counter = pageCounter, onClick = { viewModel.incrementPageController() })
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AthkarTextSlider(viewModel = viewModel, fontViewModel = fontViewModel)
        }
    }

    // — Reset button at bottom-left for counter screens —
    if (showFloatingCounter) {
        FloatingActionButton(
            onClick = { viewModel.resetPageController() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(40.dp),
            containerColor = AppColor.darkGold,
            contentColor = Color.White,
            shape = CircleShape,
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "إعادة تعيين")
        }
    }
}
```

Update imports — add `Alignment`, `size` to imports:

```kotlin
import androidx.compose.foundation.layout.size
```

Remove unused imports if any (after doing all changes, verify imports are clean):
- `val counters by floatingCounterVM.counters.collectAsState()` — this line removed, so `collectAsState` may still be used elsewhere (yes, for `pageCounter`)

- [ ] **Step 2: Verify it compiles**

```bash
./gradlew lint || true
```

No new lint errors expected.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt
git commit -m "feat: wire FAB to page counter and add reset button"
```

---

## Verification

- [ ] Build the project:

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL

- [ ] Run lint:

```bash
./gradlew lint
```

Expected: Only pre-existing lint errors (NotificationService.kt NewApi), no new errors from these changes.
