# Architecture Overview

## MVVM in Athkarix

This project follows the **MVVM** pattern (Model-View-ViewModel). Here's what each layer does:

```
┌─────────────────────────────────────────────────────────┐
│  VIEW (Composable functions)                            │
│  ui/screens/, ui/components/                            │
│  - Displays data from ViewModel                         │
│  - Sends user actions to ViewModel                      │
│  - Reacts to state changes automatically                │
└────────────────────┬────────────────────────────────────┘
                     │ collects state (collectAsState)
                     │ sends events (onClick → ViewModel method)
                     ▼
┌─────────────────────────────────────────────────────────┐
│  VIEWMODEL                                              │
│  viewmodel/, ui/screens/*/HomeViewModel.kt              │
│  - Holds UI state as StateFlow                          │
│  - Exposes one-shot events as SharedFlow                │
│  - Calls repository/services for data                   │
│  - Survives configuration changes (rotation)            │
└────────────────────┬────────────────────────────────────┘
                     │ reads data
                     ▼
┌─────────────────────────────────────────────────────────┐
│  MODEL                                                   │
│  data/model/, data/repository/, data/text/               │
│  - Data classes (AthkarItem, AssmaHussnaItem)           │
│  - AthkarRepository (object with all athkar lists)      │
│  - Text constants (Arabic content)                      │
│  - Services (JSON loading, notifications)               │
│  - SharedPrefsManager (persistence)                     │
└─────────────────────────────────────────────────────────┘
```

## Package Map

```
com.athkarix.app/
│
├── AthkarixApp.kt              ← Application class (one-time setup)
├── MainActivity.kt             ← Entry point (single Activity)
│
├── di/
│   └── AppModule.kt            ← Manual DI (wires everything together)
│
├── navigation/
│   ├── Routes.kt               ← Route string constants
│   └── AthkarixNavGraph.kt     ← NavHost: route → screen mappings
│
├── ui/
│   ├── theme/
│   │   ├── AppColor.kt         ← Color constants (gold, dark, etc.)
│   │   └── AppTheme.kt         ← Material dark theme setup
│   ├── screens/
│   │   ├── home/               ← Home menu screen + ViewModel
│   │   ├── athkar/             ← Generic athkar reader screen
│   │   ├── assma_hussna/       ← 99 Names screen (loading/error/content)
│   │   ├── search/             ← Search screen + ViewModel
│   │   └── settings/           ← Notification settings screen
│   └── components/
│       ├── AthkarTextSlider.kt ← Swipeable athkar pages
│       ├── CustomButton.kt     ← Gold rounded button
│       ├── CustomDrawer.kt     ← Navigation drawer
│       ├── FloatingCounterFab.kt ← Tasbeeh counter button
│       ├── FontControls.kt     ← Font size +/- controls
│       └── AlertExitApp.kt     ← Exit confirmation dialog
│
├── viewmodel/
│   ├── BaseAthkarViewModel.kt  ← Abstract base (core logic)
│   ├── AthkarSabahViewModel.kt ← Morning athkar (custom counters)
│   ├── AthkarMassaViewModel.kt ← Evening athkar
│   ├── ... (9 more athkar VMs)  ← Other categories
│   ├── AssmaHussnaViewModel.kt ← 99 Names (JSON loading)
│   ├── FontViewModel.kt        ← Font size/family state
│   ├── FloatingCounterViewModel.kt ← Tasbeeh counter state
│   └── NotificationSettingsViewModel.kt ← Notification prefs
│
├── data/
│   ├── model/
│   │   ├── AthkarItem.kt       ← Main data class (duaText + footer)
│   │   └── AssmaHussnaItem.kt  ← From JSON (id, name, text)
│   ├── repository/
│   │   └── AthkarRepository.kt ← All athkar lists in one place
│   ├── text/                   ← Arabic text constants (auto-generated)
│   │   ├── AthkarSabahText.kt
│   │   ├── AthkarMassaText.kt
│   │   └── ... (9 more)
│   ├── local/
│   │   └── SharedPrefsManager.kt ← Preferences wrapper
│   └── service/
│       ├── NotificationService.kt ← Alarm + notification scheduling
│       └── AssmaHussnaService.kt  ← JSON loader + parser
│
└── util/
    ├── FontScaleUtil.kt        ← Tablet font scaling
    ├── ShareUtil.kt            ← Share text via Intent
    ├── WhatsAppUtil.kt         ← Open WhatsApp link
    └── DiacriticUtil.kt        ← Remove Arabic diacritics for search
```

## How Data Flows: "Morning Athkar" Example

Let's trace what happens when you tap "أذكار الصباح" (Morning Athkar) on the home screen:

### Step 1: User taps button
```
HomeScreen.kt
  → CustomButton(onClick = { viewModel.goToAthkarSabah() })
```

### Step 2: HomeViewModel emits navigation event
```kotlin
// HomeViewModel.kt
fun goToAthkarSabah() = navigate("athkar_sabah")

private fun navigate(route: String) {
    viewModelScope.launch {
        _navigationEvent.emit(HomeNavigationEvent.GoToRoute(route))
    }
}
```

### Step 3: HomeScreen collects the event and navigates
```kotlin
// HomeScreen.kt
LaunchedEffect(Unit) {
    viewModel.navigationEvent.collect { event ->
        when (event) {
            is HomeNavigationEvent.GoToRoute -> onNavigate(event.route)
        }
    }
}
```

### Step 4: NavGraph matches the route and creates the screen
```kotlin
// AthkarixNavGraph.kt
composable(Routes.ATHKAR_SABAH) {
    val vm = remember { AppModule.provideAthkarSabahViewModel() }
    AthkarScreen(
        viewModel = vm,
        fontViewModel = fontVM,
        onBack = { back() },
        onShare = { text -> ShareUtil.shareText(context, text) },
    )
}
```

### Step 5: AthkarSabahViewModel provides data to AthkarScreen
```kotlin
// AthkarSabahViewModel.kt
class AthkarSabahViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarSabahList
    override val completionMessage: String = "أتممت أذكار الصباح"
    override val maxPageCounters: List<Int> = listOf(1, 1, 3, 1, 1, 1, 4, ...)
}
```

### Step 6: AthkarScreen renders AthkarTextSlider
```kotlin
// AthkarTextSlider.kt
val pageIndex by viewModel.currentPageIndex.collectAsState()
HorizontalPager(state = pagerState, reverseLayout = true) { page ->
    Text(text = viewModel.dataList[page].duaText, ...)
}
```

## State vs. Events

The codebase uses two distinct patterns for different kinds of data:

| | StateFlow | SharedFlow |
|---|---|---|
| **Purpose** | Continuous state | One-shot events |
| **Has current value?** | Yes (always accessible) | No (fire and forget) |
| **Example** | `fontSize`, `currentPageIndex` | `eventFlow` (navigation, snackbar) |
| **Compose API** | `val x by viewModel.state.collectAsState()` | `LaunchedEffect { flow.collect { } }` |

## Key Architectural Decisions

1. **Single Activity** — The entire app lives in one Activity. Screens are swapped via Jetpack Navigation Compose, not separate Activities.

2. **Manual DI** — Instead of Hilt/Dagger, the app uses a simple `object AppModule` with `?:` (Elvis operator) caching. This is simpler to understand and has no annotation magic.

3. **No Network** — All data is local. Athkar text is stored as Kotlin constants. The 99 Names come from a JSON file in `assets/`. Preferences use `SharedPreferences`.

4. **Abstract ViewModel** — 11 athkar categories share the same behavior through `BaseAthkarViewModel`. Only the data (lists, messages, counter requirements) differs.

5. **Arabic-First** — The UI is RTL. `HorizontalPager` uses `reverseLayout = true`. All text is in Arabic.
