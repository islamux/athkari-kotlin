# Component Extraction & Reorganization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Extract inline composables from screen files into `ui/components/` subdirectories by concern, then reorganize existing components into the same structure. No behavior changes.

**Architecture:** Each new component is a self-contained Composable function extracted from screen files. Existing components get relocated to matching subdirectories with updated packages. Screens import from the new locations.

**Tech Stack:** Kotlin, Jetpack Compose, Material3

**Branch:** `refactor/extract-navigation-drawer` (already created)

---

## File Structure

### New files to create (7):
- `ui/components/common/AthkarixTopAppBar.kt`
- `ui/components/common/LoadingErrorContent.kt`
- `ui/components/common/BackgroundImage.kt`
- `ui/components/common/ExitGuard.kt`
- `ui/components/dua/DuaContent.kt`
- `ui/components/search/SearchTextField.kt`
- `ui/components/notification/NotificationToggleRow.kt`

### Existing files to move (6):
- `ui/components/CustomButton.kt` → `ui/components/common/CustomButton.kt`
- `ui/components/AlertExitApp.kt` → `ui/components/common/AlertExitApp.kt`
- `ui/components/FloatingCounterFab.kt` → `ui/components/navigation/FloatingCounterFab.kt`
- `ui/components/CustomDrawer.kt` → `ui/components/navigation/CustomDrawer.kt`
- `ui/components/AthkarTextSlider.kt` → `ui/components/dua/AthkarTextSlider.kt`
- `ui/components/FontControls.kt` → `ui/components/dua/FontControls.kt`

### Screen files to modify (6):
- `ui/screens/home/HomeScreen.kt`
- `ui/screens/assma_hussna/AssmaHussnaScreen.kt`
- `ui/screens/athkar/AthkarScreen.kt`
- `ui/screens/search/SearchResultScreen.kt`
- `ui/screens/search/SearchScreen.kt`
- `ui/screens/settings/NotificationSettingsScreen.kt`

### Navigation file to modify (1):
- `navigation/AthkarixNavGraph.kt` (import paths for existing moved components)

---

### Task 1: Extract `common/AthkarixTopAppBar`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/common/AthkarixTopAppBar.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/assma_hussna/AssmaHussnaScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchResultScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/settings/NotificationSettingsScreen.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt`

- [ ] **Step 1: Read AssmaHussnaScreen.kt to capture the TopAppBar inline code**

Run: `cat` to see lines 51-63 (the TopAppBar with back button and title)

- [ ] **Step 2: Create `common/AthkarixTopAppBar.kt`**

```kotlin
package com.athkarix.app.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.athkarix.app.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarixTopAppBar(
    title: String = "",
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = AppColor.primaryGold,
            )
        },
        navigationIcon = onBack?.let { back ->
            {
                IconButton(onClick = back) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "رجوع",
                        tint = AppColor.primaryGold,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = AppColor.primaryGold,
        ),
        modifier = modifier,
    )
}
```

- [ ] **Step 3: Update AssmaHussnaScreen.kt**

Replace the inline TopAppBar with `AthkarixTopAppBar`. Read the file first, then replace the TopAppBar block and update imports.

- [ ] **Step 4: Update SearchResultScreen.kt**

Same pattern — replace inline TopAppBar with AthkarixTopAppBar.

- [ ] **Step 5: Update NotificationSettingsScreen.kt**

Same pattern.

- [ ] **Step 6: Update AthkarScreen.kt**

Replace the inline TopAppBar with AthkarixTopAppBar.

- [ ] **Step 7: Run lint to verify**

```bash
./gradlew lint
```

- [ ] **Step 8: Commit**

```bash
git add .
git commit -m "feat(components): extract AthkarixTopAppBar from 4 screens"
```

---

### Task 2: Extract `dua/DuaContent`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/dua/DuaContent.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchResultScreen.kt`

- [ ] **Step 1: Read SearchResultScreen.kt to capture the dua text + footer rendering block (lines 54-76)**

- [ ] **Step 2: Create `dua/DuaContent.kt`**

```kotlin
package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun DuaContent(
    duaText: String?,
    footer: String?,
    fontFamily: String = "Amiri",
    fontSize: Float = 28.6f,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = duaText ?: "",
            fontFamily = if (fontFamily == "Amiri") FontFamily.Serif else FontFamily.SansSerif,
            fontSize = fontSize.sp,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(Modifier.height(16.dp))
        footer?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = AppColor.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}
```

- [ ] **Step 3: Update SearchResultScreen.kt**

Replace the inline Column with `DuaContent`. Remove the now-unused imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract DuaContent from SearchResultScreen"
```

---

### Task 3: Extract `common/LoadingErrorContent`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/common/LoadingErrorContent.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/assma_hussna/AssmaHussnaScreen.kt`

- [ ] **Step 1: Read AssmaHussnaScreen.kt to capture the loading/error/retry pattern (lines 67-89)**

- [ ] **Step 2: Create `common/LoadingErrorContent.kt`**

```kotlin
package com.athkarix.app.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun LoadingErrorContent(
    isLoading: Boolean,
    hasError: Boolean,
    errorMessage: String,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = AppColor.primaryGold)
            }
        }
        hasError -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = errorMessage,
                    color = AppColor.textSecondary,
                )
                TextButton(onClick = onRetry) {
                    Text("إعادة المحاولة", color = AppColor.primaryGold)
                }
            }
        }
        else -> content()
    }
}
```

- [ ] **Step 3: Update AssmaHussnaScreen.kt**

Replace the inline `when` with `LoadingErrorContent`. Clean up imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract LoadingErrorContent from AssmaHussnaScreen"
```

---

### Task 4: Extract `common/BackgroundImage`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/common/BackgroundImage.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt`

- [ ] **Step 1: Read HomeScreen.kt to capture the background image pattern (lines 153-158)**

- [ ] **Step 2: Create `common/BackgroundImage.kt`**

```kotlin
package com.athkarix.app.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.athkarix.app.R

@Composable
fun BackgroundImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.bg_home),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
    )
}
```

- [ ] **Step 3: Update HomeScreen.kt**

Replace the inline Image block with `BackgroundImage()`. Clean up imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract BackgroundImage from HomeScreen"
```

---

### Task 5: Extract `common/ExitGuard`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/common/ExitGuard.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/home/HomeScreen.kt`

- [ ] **Step 1: Read HomeScreen.kt to capture BackHandler + AlertExitApp pattern (lines 97-109)**

- [ ] **Step 2: Create `common/ExitGuard.kt`**

```kotlin
package com.athkarix.app.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.athkarix.app.ui.components.common.AlertExitApp

@Composable
fun ExitGuard(
    showDialog: Boolean,
    onRequestExit: () -> Unit,
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(enabled = true, onBack = onRequestExit)

    if (showDialog) {
        AlertExitApp(
            onConfirmExit = onConfirmExit,
            onDismiss = onDismiss,
        )
    }
}
```

- [ ] **Step 3: Update HomeScreen.kt**

Replace the inline BackHandler + AlertExitApp block with `ExitGuard(...)`. Clean up imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract ExitGuard from HomeScreen"
```

---

### Task 6: Extract `search/SearchTextField`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/search/SearchTextField.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/search/SearchScreen.kt`

- [ ] **Step 1: Read SearchScreen.kt to capture the search TextField (lines 45-77)**

- [ ] **Step 2: Create `search/SearchTextField.kt`**

```kotlin
package com.athkarix.app.ui.components.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("بحث") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "بحث")
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColor.primaryGold,
            cursorColor = AppColor.primaryGold,
            focusedLabelColor = AppColor.primaryGold,
        ),
        modifier = modifier.fillMaxWidth().padding(16.dp),
    )
}
```

- [ ] **Step 3: Update SearchScreen.kt**

Replace the inline OutlinedTextField block with `SearchTextField(...)`. Clean up imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract SearchTextField from SearchScreen"
```

---

### Task 7: Extract `notification/NotificationToggleRow`

**Files:**
- Create: `app/src/main/java/com/athkarix/app/ui/components/notification/NotificationToggleRow.kt`
- Modify: `app/src/main/java/com/athkarix/app/ui/screens/settings/NotificationSettingsScreen.kt`

- [ ] **Step 1: Read NotificationSettingsScreen.kt to capture the toggle row pattern (lines 65-103)**

- [ ] **Step 2: Create `notification/NotificationToggleRow.kt`**

```kotlin
package com.athkarix.app.ui.components.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun NotificationToggleRow(
    label: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    timeText: String?,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.weight(1f), color = AppColor.amber)
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
            colors = SwitchDefaults.colors(checkedTrackColor = AppColor.primaryGold),
        )
    }
    if (enabled && timeText != null) {
        TextButton(
            onClick = onTimeClick,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(timeText, color = AppColor.primaryGold)
        }
    }
    Spacer(Modifier.height(8.dp))
}
```

- [ ] **Step 3: Update NotificationSettingsScreen.kt**

Replace the two inline Row+Switch blocks with `NotificationToggleRow(...)`. Clean up imports.

- [ ] **Step 4: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat(components): extract NotificationToggleRow from NotificationSettingsScreen"
```

---

### Task 8: Relocate existing components into subdirectories

**Files to move (6):**
- `ui/components/CustomButton.kt` → `ui/components/common/CustomButton.kt`
- `ui/components/AlertExitApp.kt` → `ui/components/common/AlertExitApp.kt`
- `ui/components/CustomDrawer.kt` → `ui/components/navigation/CustomDrawer.kt`
- `ui/components/FloatingCounterFab.kt` → `ui/components/navigation/FloatingCounterFab.kt`
- `ui/components/AthkarTextSlider.kt` → `ui/components/dua/AthkarTextSlider.kt`
- `ui/components/FontControls.kt` → `ui/components/dua/FontControls.kt`

**Imports to update across the project for each moved file.**

- [ ] **Step 1: For each component, update its package declaration and move the file**

```bash
# Create subdirectory structure
mkdir -p app/src/main/java/com/athkarix/app/ui/components/{common,navigation,dua,search,notification}

# Move and update packages for each file
# For each file: update package from com.athkarix.app.ui.components
# to com.athkarix.app.ui.components.{subdirectory}
```

Then for each moved component:

- [ ] **Step 2: Update package declaration in the moved file**

Change `package com.athkarix.app.ui.components` to `package com.athkarix.app.ui.components.{subdir}`

- [ ] **Step 3: Update all imports across the project**

Search for `import com.athkarix.app.ui.components.ComponentName` and replace with `import com.athkarix.app.ui.components.{subdir}.ComponentName`

Affected files include:
- HomeScreen.kt (CustomDrawer, CustomButton, AlertExitApp)
- AssmaHussnaScreen.kt (AthkarTextSlider)
- AthkarScreen.kt (AthkarTextSlider, FontControls, FloatingCounterFab)
- SearchScreen.kt
- SearchResultScreen.kt
- AlertExitApp.kt (CustomButton)
- AthkarixNavGraph.kt

- [ ] **Step 4: Delete the old files from `ui/components/`**

```bash
rm app/src/main/java/com/athkarix/app/ui/components/CustomButton.kt
rm app/src/main/java/com/athkarix/app/ui/components/AlertExitApp.kt
rm app/src/main/java/com/athkarix/app/ui/components/CustomDrawer.kt
rm app/src/main/java/com/athkarix/app/ui/components/FloatingCounterFab.kt
rm app/src/main/java/com/athkarix/app/ui/components/AthkarTextSlider.kt
rm app/src/main/java/com/athkarix/app/ui/components/FontControls.kt
```

- [ ] **Step 5: Run lint**

```bash
./gradlew lint
```

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "refactor(components): reorganize existing components into subdirectories"
```

---

## Verification

After all tasks:
```bash
./gradlew lint
./gradlew test
```

## Rollback

If any task breaks compilation, revert the last commit and fix:
```bash
git revert HEAD
```
