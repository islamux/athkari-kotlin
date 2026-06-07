# UI Layer — Compose Screens and Components

## Overview

The UI is built entirely with **Jetpack Compose** — Kotlin functions that describe what the screen looks like. There are no XML layout files for the main UI (only themes and resources).

The entry point is tiny:

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AthkarixTheme {
                AthkarixNavGraph(navController = rememberNavController())
            }
        }
    }
}
```

This says: "Use the dark gold theme, and show the navigation graph." Everything else is a screen inside that graph.

---

## Theme

### AppColor — Color Constants

```kotlin
// ui/theme/AppColor.kt
object AppColor {
    val primaryGold = Color(0xFFFFD700)    // #FFD700
    val darkGold = Color(0xFFD4AF37)        // #D4AF37
    val amber = Color(0xFFFFBF00)            // #FFBF00
    val background = Color(0xFF000000)       // Black
    val surface = Color(0xFF1A1A1A)          // Dark gray
    val textPrimary = Color(0xFFFFD700)      // Gold text
    val textSecondary = Color(0xFFCCCCCC)    // Light gray
    val footer = Color(0xFFB0B0B0)           // Gray for citations
    val ayahHadith = Color(0xFF9C27B0)       // Purple for Quran/Hadith
}
```

Colors are defined in a single `object` so they're easy to change and consistent across the app.

### AppTheme — Material Dark Theme

```kotlin
// ui/theme/AppTheme.kt
private val DarkColorScheme = darkColorScheme(
    primary = AppColor.primaryGold,
    onPrimary = AppColor.background,
    secondary = AppColor.amber,
    background = AppColor.background,
    surface = AppColor.surface,
    onBackground = AppColor.textPrimary,
    onSurface = AppColor.textPrimary,
)

@Composable
fun AthkarixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content,
    )
}
```

Uses Material 3's `darkColorScheme()`. The app is **always dark mode** — no light theme support.

---

## Screen-by-Screen Tour

### HomeScreen

The main menu. Structure:

```kotlin
ModalNavigationDrawer(
    drawerContent = { CustomDrawer(...) }
) {
    Scaffold(
        topBar = { TopAppBar with search icon }
    ) {
        // Background image (bg_home.jpg)
        // LazyColumn of 11 CustomButtons
        // BackHandler shows AlertExitApp dialog
    }
}
```

Each button is a `CustomButton`:

```kotlin
CustomButton(
    icon = Icons.Default.Star,  // icon changes per category
    text = "أذكار الصباح",
    onClick = { viewModel.goToAthkarSabah() },
)
```

When the user presses back, an `AlertExitApp` dialog asks "هل أنهيت أذكارك؟" (Did you finish your athkar?).

### AthkarScreen — Generic Reading Screen

This is the most important screen — it's reused by all 11 athkar categories. The navigation graph passes different ViewModels to the same composable:

```kotlin
// AthkarixNavGraph.kt
composable(Routes.ATHKAR_SABAH) {
    val vm = remember { AppModule.provideAthkarSabahViewModel() }
    AthkarScreen(viewModel = vm, ...)
}
composable(Routes.TASBIH) {
    val vm = remember { AppModule.provideTasbihViewModel() }
    AthkarScreen(viewModel = vm, floatingCounterVM = ..., showFloatingCounter = true, ...)
}
```

The screen collects state from the ViewModel:

```kotlin
val pageIndex by viewModel.currentPageIndex.collectAsState()
val pageCounter by viewModel.currentPageCounter.collectAsState()
```

And listens for one-shot events:

```kotlin
LaunchedEffect(Unit) {
    viewModel.eventFlow.collect { event ->
        when (event) {
            is ViewEvent.ShowCompletion -> {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }
}
```

### AssmaHussnaScreen — 99 Names

This screen demonstrates the **loading/error/content** pattern:

```kotlin
val isLoading by viewModel.isLoading.collectAsState()
val hasError by viewModel.hasError.collectAsState()

when {
    isLoading -> CircularProgressIndicator(...)
    hasError -> Column { Text(errorMsg); Button("إعادة المحاولة") }
    else -> AthkarTextSlider(viewModel, fontViewModel)
}
```

### SearchScreen

A text field + results list:

```kotlin
OutlinedTextField(
    value = viewModel.query.collectAsState().value,
    onValueChange = { viewModel.search(it) },
    trailingIcon = { if (query.isNotEmpty()) Icon(Close) }
)

LazyColumn {
    items(viewModel.results.collectAsState().value) { result ->
        ListItem(
            headlineContent = { Text(result.item.duaText) },
            supportingContent = { Text(result.category) },
            onClick = { onResultClick(result) }
        )
    }
}
```

### NotificationSettingsScreen

Two rows, each with a `Switch` and a time button:

```kotlin
Row {
    Text("أذكار الصباح")
    Switch(checked = morningEnabled, onCheckedChange = { vm.toggleMorning() })
    TextButton(onClick = { /* show TimePicker (placeholder) */ }) {
        Text("$morningHour:$morningMinute")
    }
}
```

Note: The `TimePicker` is currently a placeholder — clicking the time button does nothing yet.

---

## Key Components

### AthkarTextSlider — The Core UI Component

**File**: `ui/components/AthkarTextSlider.kt` (97 lines)

This is the heart of the athkar reading experience. It uses `HorizontalPager` for swipeable pages.

```kotlin
@Composable
fun AthkarTextSlider(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
) {
    val pageIndex by viewModel.currentPageIndex.collectAsState()
    val fontSize by fontViewModel.fontSize.collectAsState()
    val fontFamily by fontViewModel.selectedFont.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = pageIndex,
        pageCount = { viewModel.dataList.size }
    )

    // Sync pager when ViewModel advances the page programmatically
    LaunchedEffect(pageIndex) {
        if (pagerState.currentPage != pageIndex) {
            pagerState.animateScrollToPage(pageIndex)
        }
    }

    HorizontalPager(
        state = pagerState,
        reverseLayout = true,   // ← RTL: swipe right-to-left
        modifier = Modifier.clickable { viewModel.incrementPageController() }
    ) { page ->
        val item = viewModel.dataList.getOrNull(page)
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = item?.duaText ?: "",
                fontFamily = if (fontFamily == "Amiri") FontFamily.Serif else FontFamily.SansSerif,
                fontSize = fontSize.sp,
                color = AppColor.primaryGold,
                textAlign = TextAlign.Center,
            )
            if (!item?.footer.isNullOrBlank()) {
                Text(
                    text = item?.footer,
                    fontSize = (fontSize * 0.7f).sp,
                    color = AppColor.footer,
                )
            }
        }
    }
}
```

Key behaviors:
- **Tap anywhere** on the page → `viewModel.incrementPageController()` (advances counter)
- **Swipe** → changes page (calls `viewModel.onPageChanged()`)
- **Font controls** in the top bar change font size live
- **RTL**: `reverseLayout = true` makes swiping work right-to-left for Arabic

### CustomButton

```kotlin
@Composable
fun CustomButton(
    icon: ImageVector = Icons.Default.ArrowBack,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = AppColor.darkGold),
        shape = RoundedCornerShape(28.dp),   // ← pill shape
    ) {
        Icon(icon, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White)
    }
}
```

Gold pill-shaped button used everywhere (home menu, dialogs, drawer).

### FloatingCounterFab

```kotlin
@Composable
fun FloatingCounterFab(
    counter: Int,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = AppColor.darkGold,
        shape = CircleShape,
    ) {
        Text(text = "$counter", fontWeight = Bold)
    }
}
```

A circular gold FAB showing the current tasbeeh count. Appears on Tasbih, Estigfar, Hamd, and Salat screens.

### FontControls

```kotlin
@Composable
fun FontControls(fontViewModel: FontViewModel) {
    Row {
        IconButton(onClick = { fontViewModel.increaseFontSize() }) { Icon(Add) }
        IconButton(onClick = { fontViewModel.decreaseFontSize() }) { Icon(Remove) }
    }
}
```

Plus/minus buttons in the top bar to adjust text size.

### AlertExitApp

```kotlin
@Composable
fun AlertExitApp(showDialog: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            containerColor = AppColor.darkGold,  // gold background
            title = { Text("تنبيه!") },
            text = { Text("هل أنهيت أذكارك؟") },
            confirmButton = { CustomButton(text = "نعم", onClick = onConfirm) },
            dismissButton = { CustomButton(text = "لا", onClick = onDismiss) },
        )
    }
}
```

Exit confirmation: "Alert! Have you finished your athkar?"

---

## How State Flows to the UI

Every screen follows this pattern:

```
ViewModel.StateFlow  ──collectAsState()──▶  Composable
                                          │
ViewModel.method()  ◀────────onClick──────┘
```

Example in `AthkarTextSlider`:

```kotlin
// 1. Collect state from ViewModel
val pageIndex by viewModel.currentPageIndex.collectAsState()

// 2. Render based on state
Text(
    text = dataList[pageIndex].duaText,
    fontSize = fontSize.sp,        // ← from FontViewModel
)

// 3. User action → ViewModel
Modifier.clickable { viewModel.incrementPageController() }
```

When `viewModel.incrementPageController()` changes `_currentPageIndex.value`, every Composable collecting `currentPageIndex` automatically recomposes.
