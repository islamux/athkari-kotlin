# Navigation and Dependency Injection

## Single-Activity Architecture

Athkarix uses a **single-Activity** architecture:

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

"Single Activity" means the app has exactly one Android `Activity`. Every "screen" in the app is just a different Composable shown by the `NavHost`. This is modern Android best practice — you don't need multiple Activities.

---

## Navigation: Routes

**File**: `navigation/AthkarixNavGraph.kt`

### Routes Object

All route names are defined in one `object`:

```kotlin
object Routes {
    const val HOME = "home"
    const val ATHKAR_SABAH = "athkar_sabah"
    const val ASSMA_HUSSNA = "assma_hussna"
    const val SEARCH = "search"
    const val SEARCH_RESULT = "search_result/{categoryIndex}/{itemIndex}"
    // ... and 10 more
}
```

Route constants avoid typos — instead of writing `"home"` in multiple places, you write `Routes.HOME` and the compiler checks it.

The `SEARCH_RESULT` route has **path parameters** — `{categoryIndex}` and `{itemIndex}` get replaced with actual values at runtime:

```kotlin
navController.navigate("search_result/athkar_sabah/3")
```

### NavHost

The `NavHost` maps routes to composables:

```kotlin
NavHost(navController = navController, startDestination = Routes.HOME) {
    composable(Routes.HOME) {
        // Create ViewModel and show HomeScreen
    }
    composable(Routes.ATHKAR_SABAH) {
        // Create ViewModel and show AthkarScreen
    }
    // ... 12 more routes
}
```

Navigation between screens:

```kotlin
// Navigate to a destination
navController.navigate("athkar_sabah")

// Go back
navController.popBackStack()
```

### Route with Arguments

```kotlin
composable(
    route = Routes.SEARCH_RESULT,
    arguments = listOf(
        navArgument("categoryIndex") { type = NavType.StringType },
        navArgument("itemIndex") { type = NavType.IntType },
    )
) { entry ->
    val categoryIndex = entry.arguments?.getString("categoryIndex") ?: ""
    val itemIndex = entry.arguments?.getInt("itemIndex") ?: 0
    PlaceholderScreenWithVM(name = "نتيجة البحث", ...)
}
```

Arguments are extracted from `entry.arguments` and typed using `NavType`.

---

## Dependency Injection: AppModule

**File**: `di/AppModule.kt`

### What Is Dependency Injection?

"Dependency Injection" (DI) is a fancy term for: "instead of creating objects inside the classes that need them, create them in one central place and pass them in."

Without DI:
```kotlin
class AthkarScreen() {
    // BAD: Screen creates its own ViewModel — can't reuse or swap
    val vm = AthkarSabahViewModel()
}
```

With DI:
```kotlin
// GOOD: Screen receives its dependencies
@Composable
fun AthkarScreen(viewModel: BaseAthkarViewModel, ...) {
    // Doesn't know or care which specific ViewModel — just uses it
}
```

### Manual DI via Object

Athkarix doesn't use Hilt, Dagger, or Koin. Instead, it uses a simple `object`:

```kotlin
object AppModule {
    // Cached singletons (nullable backing fields)
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var fontViewModel: FontViewModel? = null
    private var floatingCounterViewModel: FloatingCounterViewModel? = null

    // Singleton providers (same instance every time)
    fun provideFontViewModel(): FontViewModel {
        if (fontViewModel == null) {
            fontViewModel = FontViewModel()
        }
        return fontViewModel!!  // safe because we just assigned it
    }

    // Fresh instance providers (new instance every time)
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel()
    fun provideAthkarSabahViewModel(): AthkarSabahViewModel = AthkarSabahViewModel()
    fun provideAthkarMassaViewModel(): AthkarMassaViewModel = AthkarMassaViewModel()
    // ...

    // Context-dependent providers
    fun provideAssmaHussnaViewModel(context: Context): AssmaHussnaViewModel =
        AssmaHussnaViewModel(context)

    // Composite provider (combines multiple dependencies)
    fun provideNotificationSettingsViewModel(context: Context): NotificationSettingsViewModel =
        NotificationSettingsViewModel(
            provideSharedPrefsManager(context),
            provideNotificationService(context),
        )
}
```

### Singleton vs. Fresh Instance

| Strategy | Providers | Why? |
|----------|-----------|------|
| **Singleton** (cached) | `FontViewModel`, `FloatingCounterViewModel`, `SharedPrefsManager` | Shared across all screens — font size should be the same everywhere |
| **Fresh instance** | `AthkarSabahViewModel`, `HomeViewModel`, etc. | Each category has its own state — page position, counter |
| **Composite** | `NotificationSettingsViewModel` | Needs multiple dependencies wired together |

### How NavGraph Uses AppModule

```kotlin
@Composable
fun AthkarixNavGraph(navController: NavHostController) {
    // Shared ViewModels — created once for the entire navigation graph
    val fontVM = remember { AppModule.provideFontViewModel() }
    val floatingCounterVM = remember { AppModule.provideFloatingCounterViewModel() }

    NavHost(...) {
        composable(Routes.ATHKAR_SABAH) {
            // Per-screen ViewModel — created fresh for each navigation
            val vm = remember { AppModule.provideAthkarSabahViewModel() }
            AthkarScreen(viewModel = vm, fontViewModel = fontVM, ...)
        }

        composable(Routes.TASBIH) {
            val vm = remember { AppModule.provideTasbihViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,  // shared across Tasbih/Estigfar/Hamd/Salat
                showFloatingCounter = true,
                ...
            )
        }
    }
}
```

The `remember { }` call is critical. Without it, a new ViewModel would be created on every recomposition (every frame). `remember` ensures the ViewModel is created once and kept alive as long as the Composable is in the composition.

### Why This Pattern?

Manual DI (no framework) was chosen because:
1. **Simple** — no annotations, no generated code, no learning curve
2. **Small project** — ~50 source files, no need for a DI framework
3. **Transparent** — you can see exactly how every dependency is created

The trade-off: ViewModels don't survive process death. If Android kills the app and recreates it, all ViewModel state is lost. A proper DI framework with `SavedStateHandle` would fix this, but it adds complexity.

---

## The Big Picture: How It All Connects

```
MainActivity.kt
    │
    ▼
AthkarixTheme  ← AppColor.kt, AppTheme.kt
    │
    ▼
AthkarixNavGraph ← Routes.kt
    │
    ├── remember { AppModule.provideFontViewModel() }   ← shared
    ├── remember { AppModule.provideFloatingCounterViewModel() } ← shared
    │
    ├── composable(HOME) → remember { AppModule.provideHomeViewModel() }
    │                      → HomeScreen(viewModel, onNavigate)
    │
    ├── composable(ATHKAR_SABAH) → remember { AppModule.provideAthkarSabahViewModel() }
    │                              → AthkarScreen(viewModel, fontVM)
    │
    ├── composable(TASBIH) → remember { AppModule.provideTasbihViewModel() }
    │                         → AthkarScreen(viewModel, fontVM, floatingCounterVM, showFloatingCounter=true)
    │
    ├── composable(ASSMA_HUSSNA) → remember { AppModule.provideAssmaHussnaViewModel(context) }
    │                               → AssmaHussnaScreen(viewModel, fontVM)
    │
    ├── composable(SEARCH) → remember { SearchViewModel() }
    │                         → SearchScreen(viewModel, onResultClick)
    │
    ├── composable(NOTIFICATION_SETTINGS) → remember { AppModule.provideNotificationSettingsViewModel(context) }
    │                                        → NotificationSettingsScreen(viewModel)
    │
    └── composable(SEARCH_RESULT/{cat}/{idx}) → → PlaceholderScreenWithVM(...)
```

## Key File Cheatsheet

| File | Role | Key concept |
|------|------|-------------|
| `MainActivity.kt` | Entry point | `setContent { Theme { NavGraph } }` |
| `AthkarixNavGraph.kt` | Route → Screen mapping | `NavHost`, `composable()`, `navArgument()` |
| `Routes.kt` | Route constants | `object Routes { const val ... }` |
| `AppModule.kt` | Dependency wiring | `object AppModule`, `provide*()`, `?:` caching |
| `AthkarixApp.kt` | Application class | `companion object { lateinit var instance }` |

## How to Add a New Screen

1. Add a route constant in `Routes.kt`
2. Create the screen Composable and ViewModel
3. Wire the ViewModel in `AppModule.kt` (if not standalone)
4. Add a `composable()` entry in `AthkarixNavGraph.kt`
5. Add a navigation call from wherever triggers it (e.g., a button in `HomeViewModel`)
