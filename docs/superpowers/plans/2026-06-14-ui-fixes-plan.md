# UI Fixes & Enhancements — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use subagent-driven-development or executing-plans. Steps use checkbox (`- [ ]`) syntax.

**Goal:** Fix 9 UI issues from `test-my-ui.md`: background uniformity, search back button, home button fonts, minus icon, font overlap, Cairo font switch, email contact, Sabah/Massa floating counter, per-screen counter state.

**Architecture:** All changes are UI-layer edits to existing screens, ViewModels, and nav graph. No new screens, no data layer changes, no networking.

**Tech Stack:** Kotlin, Jetpack Compose, Material3, ViewModel + StateFlow

---

## File Overview

| File | Action | Responsibility |
|---|---|---|
| `BackgroundImage.kt` | Modify | Add optional scrim overlay parameter |
| `AthkarScreen.kt` | Modify | Add background + scrim; pass screenKey to floatingCounterVM |
| `SearchScreen.kt` | Modify | Add TopAppBar with back arrow, background + scrim |
| `NotificationSettingsScreen.kt` | Modify | Add background + scrim |
| `AthkarixNavGraph.kt` | Modify | Pass onBack to SearchScreen, floatingCounter to Sabah/Massa, screenKey to athkar screens |
| `CustomButton.kt` | Modify | Add fontSize = 18.sp |
| `CustomDrawer.kt` | Modify | Add email contact item |
| `FontControls.kt` | Modify | Change Clear to Remove icon; add font toggle button |
| `AthkarTextSlider.kt` | Modify | Add lineHeight; consume FontFamily from TTF resources |
| `FontViewModel.kt` | Modify | Add toggleFont(); keep string for display name |
| `FloatingCounterViewModel.kt` | Modify | Map-based per-screen counter storage |
| `FontViewModelTest.kt` | Modify | Update tests for toggleFont() |
| `FloatingCounterViewModelTest.kt` | Modify | Update tests for per-screen API |

---

### Task 1: Background scrim + apply to all screens

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/common/BackgroundImage.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/settings/NotificationSettingsScreen.kt`

- [ ] **Step 1: Add optional scrim to BackgroundImage**

Update `BackgroundImage.kt` to accept a `scrimAlpha` parameter:

```kotlin
package com.athkarix.app.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.athkarix.app.R

@Composable
fun BackgroundImage(
    scrimAlpha: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
            )
        }
    }
}
```

- [ ] **Step 2: Add background + scrim to AthkarScreen**

Wrap the Scaffold content in a Box with BackgroundImage behind it:

```kotlin
// Inside AthkarScreen, around the Scaffold:
Box(modifier = Modifier.fillMaxSize()) {
    BackgroundImage(scrimAlpha = 0.6f)
    Scaffold(
        // ... existing Scaffold code unchanged ...
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AthkarTextSlider(viewModel = viewModel, fontViewModel = fontViewModel)
        }
    }
}
```

- [ ] **Step 3: Add background + scrim to SearchScreen**

```kotlin
// SearchScreen.kt - wrap Column with Box + BackgroundImage:
Box(Modifier.fillMaxSize()) {
    BackgroundImage(scrimAlpha = 0.6f)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        // existing SearchTextField + results code
    }
}
```

- [ ] **Step 4: Add background + scrim to NotificationSettingsScreen**

```kotlin
// NotificationSettingsScreen.kt - wrap Scaffold with Box + BackgroundImage:
Box(modifier = Modifier.fillMaxSize()) {
    BackgroundImage(scrimAlpha = 0.6f)
    Scaffold(
        // existing Scaffold
    ) { padding ->
        Column(/* existing content */)
    }
}
```

---

### Task 2: Back button on SearchScreen

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/navigation/AthkarixNavGraph.kt`

- [ ] **Step 1: Add onBack + TopAppBar to SearchScreen**

```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onResultClick: (SearchViewModel.SearchResult) -> Unit,
    onBack: () -> Unit,
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(scrimAlpha = 0.6f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            AthkarixTopAppBar(
                onBack = onBack,
            )
            SearchTextField(
                query = query,
                onQueryChange = { viewModel.search(it) },
            )
            // existing results/empty state code unchanged
        }
    }
}
```

- [ ] **Step 2: Pass onBack from nav graph**

In `AthkarixNavGraph.kt`, update the SEARCH composable:

```kotlin
composable(Routes.SEARCH) {
    val searchVM = remember { SearchViewModel() }
    SearchScreen(
        viewModel = searchVM,
        onBack = { back() },
        onResultClick = { result ->
            navController.navigate("...")
        },
    )
}
```

---

### Task 3: Bigger home button labels

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/common/CustomButton.kt`

- [ ] **Step 1: Add fontSize to CustomButton's Text**

```kotlin
Text(
    text = text,
    color = Color.White,
    fontFamily = FontFamily.SansSerif,
    fontSize = 18.sp,
)
```

---

### Task 4: Minus icon for font decrease

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/dua/FontControls.kt`

- [ ] **Step 1: Replace Clear with Remove icon**

```kotlin
import androidx.compose.material.icons.filled.Remove

// In the composable:
IconButton(onClick = { fontViewModel.decreaseFontSize() }) {
    Icon(Icons.Default.Remove, contentDescription = "تصغير الخط")
}
IconButton(onClick = { fontViewModel.increaseFontSize() }) {
    Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
}
```

---

### Task 5: Fix font overlap at large sizes

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/dua/AthkarTextSlider.kt`

- [ ] **Step 1: Add lineHeight to main Text**

```kotlin
Text(
    text = item.duaText ?: "",
    fontFamily = fontFamily,
    fontSize = fontSize.sp,
    lineHeight = (fontSize * 1.5f).sp,
    color = AppColor.primaryGold,
    textAlign = TextAlign.Center,
)
```

Also update the footer text:

```kotlin
Text(
    text = item.footer,
    fontFamily = FontFamily.Serif,
    fontSize = (fontSize * 0.7f).sp,
    lineHeight = (fontSize * 0.7f * 1.5f).sp,
    color = AppColor.footer,
    textAlign = TextAlign.Center,
)
```

---

### Task 6: Wire Cairo TTF font + font toggle

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/viewmodel/FontViewModel.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/components/dua/AthkarTextSlider.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/components/dua/FontControls.kt`
- Modify: `app/src/test/java/com/athkarix/app/viewmodel/FontViewModelTest.kt`

- [ ] **Step 1: Add toggleFont() to FontViewModel**

```kotlin
package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FontViewModel : ViewModel() {

    private val _fontSize = MutableStateFlow(28.6f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _selectedFont = MutableStateFlow("Amiri")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    private val maxFontSize = 37.0f
    private val minFontSize = 21.0f

    fun toggleFont() {
        _selectedFont.value = if (_selectedFont.value == "Amiri") "Cairo" else "Amiri"
    }

    fun increaseFontSize() {
        if (_fontSize.value < maxFontSize) {
            val newSize = _fontSize.value + 2.0f
            _fontSize.value = if (newSize > maxFontSize) maxFontSize else newSize
        }
    }

    fun decreaseFontSize() {
        if (_fontSize.value > minFontSize) {
            val newSize = _fontSize.value - 2.0f
            _fontSize.value = if (newSize < minFontSize) minFontSize else newSize
        }
    }
}
```

- [ ] **Step 2: Update AthkarTextSlider to use TTF fonts**

Replace the font-family logic:

```kotlin
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.fontResource
import com.athkarix.app.R

// Inside the composable, after collecting state:
val selectedFontName by fontViewModel.selectedFont.collectAsState()
val fontFamily by remember {
    derivedStateOf {
        when (selectedFontName) {
            "Cairo" -> FontFamily(
                Font(R.font.cairo_regular),
                Font(R.font.cairo_bold)
            )
            else -> FontFamily(
                Font(R.font.amiri_regular),
                Font(R.font.amiri_bold)
            )
        }
    }
}

// Then in the Text composable, use fontFamily directly:
Text(
    text = item.duaText ?: "",
    fontFamily = fontFamily,
    fontSize = fontSize.sp,
    lineHeight = (fontSize * 1.5f).sp,
    color = AppColor.primaryGold,
    textAlign = TextAlign.Center,
)
```

Remove the old `val fontFamily by fontViewModel.selectedFont.collectAsState()` line (since we now have `selectedFontName` and derive `fontFamily` from it).

- [ ] **Step 3: Add font toggle button to FontControls**

```kotlin
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.athkarix.app.ui.theme.AppColor

@Composable
fun FontControls(
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier
) {
    val currentFont by fontViewModel.selectedFont.collectAsState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = { fontViewModel.decreaseFontSize() }) {
            Icon(Icons.Default.Remove, contentDescription = "تصغير الخط")
        }
        TextButton(onClick = { fontViewModel.toggleFont() }) {
            Text(
                text = currentFont,
                color = AppColor.primaryGold,
            )
        }
        IconButton(onClick = { fontViewModel.increaseFontSize() }) {
            Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
        }
    }
}
```

- [ ] **Step 4: Update FontViewModelTest**

Replace the `changeFont` test with `toggleFont`:

```kotlin
@Test
fun `toggleFont switches between Amiri and Cairo`() {
    assertEquals("Amiri", viewModel.selectedFont.value)
    viewModel.toggleFont()
    assertEquals("Cairo", viewModel.selectedFont.value)
    viewModel.toggleFont()
    assertEquals("Amiri", viewModel.selectedFont.value)
}
```

- [ ] **Step 5: Run tests**

```bash
./gradlew test --tests "com.athkarix.app.viewmodel.FontViewModelTest"
```

Expected: All 6 tests pass (5 font size tests + 1 toggle test).

---

### Task 7: Add email contact to drawer

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/ui/components/navigation/CustomDrawer.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt`

- [ ] **Step 1: Add email callback to CustomDrawer**

```kotlin
import androidx.compose.material.icons.filled.Email

@Composable
fun CustomDrawer(
    onNotificationSettings: () -> Unit,
    onContactUs: () -> Unit,
    onShare: () -> Unit,
    onEmailUs: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ... existing code ...
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
        label = { Text("تواصل معنا") },
        selected = false,
        onClick = onContactUs,
        modifier = Modifier.fillMaxWidth()
    )
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Email, contentDescription = null) },
        label = { Text("تواصل عبر البريد الإلكتروني") },
        selected = false,
        onClick = onEmailUs,
        modifier = Modifier.fillMaxWidth()
    )
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Share, contentDescription = null) },
        label = { Text("شارك التطبيق عبر وسائل التواصل") },
        selected = false,
        onClick = onShare,
        modifier = Modifier.fillMaxWidth()
    )
    // ...
}
```

- [ ] **Step 2: Wire email intent in HomeScreen**

```kotlin
// In HomeScreen, add email handler:
val onEmailUs: () -> Unit = {
    scope.launch { drawerState.close() }
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:fathi733@gmail.com")
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) { }
}
```

Update the `CustomDrawer` call in `HomeScreen`:

```kotlin
CustomDrawer(
    onNotificationSettings = { ... },
    onContactUs = { ... },
    onShare = { ... },
    onEmailUs = onEmailUs,
)
```

Add the necessary imports:
```kotlin
import android.content.Intent
import android.net.Uri
```

---

### Task 8: Floating counter for Sabah and Massa

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/navigation/AthkarixNavGraph.kt`

- [ ] **Step 1: Enable floating counter for Sabah and Massa**

Update both `ATHKAR_SABAH` and `ATHKAR_MASSA` composable calls:

```kotlin
composable(Routes.ATHKAR_SABAH) {
    val vm = remember { AppModule.provideAthkarSabahViewModel() }
    AthkarScreen(
        viewModel = vm,
        fontViewModel = fontVM,
        floatingCounterVM = floatingCounterVM,
        showFloatingCounter = true,
        onBack = { back() },
        onShare = { text -> ShareUtil.shareText(context, text) },
    )
}

composable(Routes.ATHKAR_MASSA) {
    val vm = remember { AppModule.provideAthkarMassaViewModel() }
    AthkarScreen(
        viewModel = vm,
        fontViewModel = fontVM,
        floatingCounterVM = floatingCounterVM,
        showFloatingCounter = true,
        onBack = { back() },
        onShare = { text -> ShareUtil.shareText(context, text) },
    )
}
```

---

### Task 9: Per-screen counter state (not global singleton)

**Files:**
- Modify: `app/src/main/java/com/athkarix/app/viewmodel/FloatingCounterViewModel.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/navigation/AthkarixNavGraph.kt`
- Modify: `app/src/test/java/com/athkarix/app/viewmodel/FloatingCounterViewModelTest.kt`

- [ ] **Step 1: Refactor FloatingCounterViewModel to map-based storage**

```kotlin
package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloatingCounterViewModel : ViewModel() {

    private val _counters = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counters: StateFlow<Map<String, Int>> = _counters.asStateFlow()

    fun increment(screenKey: String) {
        _counters.value = _counters.value.toMutableMap().apply {
            put(screenKey, (get(screenKey) ?: 0) + 1)
        }
    }

    fun reset(screenKey: String) {
        _counters.value = _counters.value.toMutableMap().apply {
            put(screenKey, 0)
        }
    }
}
```

- [ ] **Step 2: Update AthkarScreen to use screenKey**

Add a `screenKey` parameter and use it with the counter:

```kotlin
@Composable
fun AthkarScreen(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit,
    floatingCounterVM: FloatingCounterViewModel? = null,
    showFloatingCounter: Boolean = false,
    screenKey: String = "",
) {
    // ... existing code ...

    floatingActionButton = {
        if (showFloatingCounter && floatingCounterVM != null) {
            val counters by floatingCounterVM.counters.collectAsState()
            val count = counters[screenKey] ?: 0
            Row(
                // ... same layout ...
            ) {
                SmallFloatingActionButton(
                    onClick = { floatingCounterVM.reset(screenKey) },
                    // ...
                )
                FloatingCounterFab(
                    counter = count,
                    onClick = { floatingCounterVM.increment(screenKey) },
                )
            }
        }
    }
    // ...
}
```

- [ ] **Step 3: Pass screenKey from nav graph to every screen that uses floatingCounter**

Update all athkar composable calls in `AthkarixNavGraph.kt`. For screens with `showFloatingCounter = true`:

```kotlin
composable(Routes.TASBIH) {
    val vm = remember { AppModule.provideTasbihViewModel() }
    AthkarScreen(
        viewModel = vm,
        fontViewModel = fontVM,
        floatingCounterVM = floatingCounterVM,
        showFloatingCounter = true,
        screenKey = Routes.TASBIH,
        onBack = { back() },
        onShare = { text -> ShareUtil.shareText(context, text) },
    )
}
```

Repeat for: ESTIGFAR, HAMD, SALAT_ALA_RASOUL, ATHKAR_SABAH, ATHKAR_MASSA.

For screens without floating counter, add `screenKey` parameter with a default or empty string (already handled by default parameter `screenKey = ""`).

- [ ] **Step 4: Update FloatingCounterViewModelTest**

```kotlin
package com.athkarix.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingCounterViewModelTest {

    private val viewModel = FloatingCounterViewModel()

    @Test
    fun `increment increases counter for specific key by 1`() {
        viewModel.increment("tasbih")
        assertEquals(1, viewModel.counters.value["tasbih"])
    }

    @Test
    fun `counters for different keys are independent`() {
        viewModel.increment("tasbih")
        viewModel.increment("tasbih")
        viewModel.increment("estigfar")
        assertEquals(2, viewModel.counters.value["tasbih"])
        assertEquals(1, viewModel.counters.value["estigfar"])
    }

    @Test
    fun `reset sets counter to 0 for specific key`() {
        viewModel.increment("tasbih")
        viewModel.increment("tasbih")
        viewModel.reset("tasbih")
        assertEquals(0, viewModel.counters.value["tasbih"])
    }

    @Test
    fun `reset does not affect other keys`() {
        viewModel.increment("tasbih")
        viewModel.increment("estigfar")
        viewModel.reset("tasbih")
        assertEquals(0, viewModel.counters.value["tasbih"] ?: 0)
        assertEquals(1, viewModel.counters.value["estigfar"] ?: 0)
    }
}
```

- [ ] **Step 5: Run tests**

```bash
./gradlew test --tests "com.athkarix.app.viewmodel.FloatingCounterViewModelTest"
```

Expected: All 4 tests pass.

---

### Verification

```bash
./gradlew lint test
```

Expected: No lint errors, all tests pass.
