# UI Fixes — Design Doc

## Changes

### 1. App Icon
Replace all density-specific `ic_launcher.png` files with the existing `launcher_icon.png` content. Update `AndroidManifest.xml` to reference `@mipmap/launcher_icon`.

### 2. Font Weight Normalization
- `AppTheme.kt`: `headlineLarge` and `titleLarge` fontWeight `Bold` → `Normal`
- `CustomDrawer.kt`: title `FontWeight.Bold` → `FontWeight.Normal`

### 3. Home Screen 7 Buttons Visible
`HomeScreen.kt`: Change `Arrangement.Center` → `Arrangement.Top` so buttons start from top. The `verticalScroll` already allows scrolling to the remaining 4.

### 4. WhatsApp Fix
`WhatsAppUtil.kt`: Country code `"YE"` → `"967"` (Yemen numeric code). Fixes `wa.me/967772699924`.

### 5. Notification Text Black
`NotificationToggleRow.kt`: Label `AppColor.textPrimary` → `Color.Black`, time `AppColor.primaryGold` → `Color.Black`.

### 6. Editable Notification Hours
`NotificationSettingsScreen.kt`: Replace `/* show TimePicker */` stubs with actual `TimePickerDialog` that calls `viewModel.setMorningTime()` / `viewModel.setEveningTime()`.

### 7. Search Result Full Features
`SearchResultScreen.kt`: Add share `IconButton` in toolbar, pass `screenTitle`, wire `onShare` → `ShareUtil.shareText`. Background already present.

### 8. Counter Right, Reset Left
`AthkarScreen.kt`: Move counter FAB from Scaffold `floatingActionButton` to Box at `Alignment.BottomEnd`. Reset stays at `Alignment.BottomStart`.

### 9. Smaller Toolbar Font
`AthkarixTopAppBar.kt`: Add `fontSize = 16.sp` to title `Text`.

### 10. Remove Search From Reader Pages
- `AthkarScreen.kt`: Remove search `IconButton` from toolbar actions, remove `onSearch` parameter
- `AthkarixNavGraph.kt`: Remove all `onSearch` lines from `AthkarScreen` calls

### 11. Sidebar Drawer Indicator
`HomeScreen.kt`: Add `IconButton` with `Icons.Default.Menu` in `navigationIcon` of `TopAppBar` that opens `drawerState`.
