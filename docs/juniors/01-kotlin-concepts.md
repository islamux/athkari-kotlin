# Kotlin Concepts Used in Athkarix

This document explains every Kotlin language feature used in this project. Each section has a plain-English explanation plus a real code example from this codebase.

---

## 1. `val` vs `var`

- **`val`** — read-only reference (like `final` in Java). You assign it once, then it cannot change.
- **`var`** — mutable reference. You can reassign it.

```kotlin
// From AthkarItem.kt
data class AthkarItem(
    val duaText: String?,   // readable once set, cannot change
    val footer: String? = null,
)
```

The `val` properties of `AthkarItem` are set when the object is created and never change — the item itself is immutable.

```kotlin
// From BaseAthkarViewModel.kt
private val _currentPageIndex = MutableStateFlow(0)  // val: the reference stays the same
val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()
```

Even though `_currentPageIndex` is `val`, the *contents* of the `MutableStateFlow` can change. The `val` just means the `_currentPageIndex` variable always points to the same `MutableStateFlow` object.

---

## 2. Functions (`fun`)

```kotlin
// From DiacriticUtil.kt
object DiacriticUtil {
    private val diacritics = Regex("[\u064B-\u065F\u0670]")

    fun remove(text: String): String = text.replace(diacritics, "")
    //    ↑            ↑           ↑
    //  name      parameter    return type
}
```

Key points:
- Parameters are typed: `text: String`
- Return type comes after `:`: `: String`
- The `= expression` syntax is a **single-expression function** (no curly braces needed)
- If a function returns `Unit` (like `void` in Java), you can omit the return type

```kotlin
// From FloatingCounterViewModel.kt — function returning Unit (void)
fun increment() {
    _counter.value++
}
```

---

## 3. `object` — Singleton Pattern

An `object` declaration creates exactly ONE instance. No `new` keyword, no constructor call. It's Kotlin's built-in singleton pattern.

```kotlin
// From AppColor.kt
object AppColor {
    val primaryGold = Color(0xFFFFD700)
    val darkGold = Color(0xFFD4AF37)
    val background = Color(0xFF000000)
    val surface = Color(0xFF1A1A1A)
    val textPrimary = Color(0xFFFFD700)
}

// Usage anywhere in the app:
// AppColor.primaryGold  ← no instance needed, it's already there
```

Other `object` singletons in this project:
- `AppModule` — DI container
- `AthkarRepository` — data hub
- `AssmaHussnaService` — JSON loader
- `ShareUtil`, `WhatsAppUtil`, `DiacriticUtil` — stateless utilities
- `Routes` — route constants

Think of `object` as: "I need a class, but there should only ever be one of them."

---

## 4. `data class`

A `data class` automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`, and destructuring operators. It's perfect for holding data.

```kotlin
// From AthkarItem.kt
data class AthkarItem(
    val duaText: String?,
    val footer: String? = null,   // default value — you can omit this parameter
)
```

This tiny declaration gives you:

```kotlin
val item = AthkarItem(duaText = "اللهم...")
val item2 = item.copy(duaText = "اللهم something else")  // copy with changes
println(item)  // AthkarItem(duaText=اللهم..., footer=null)
item == item2  // true if all properties match
```

Another example from `SearchViewModel.kt`:

```kotlin
data class SearchResult(
    val category: String,
    val categoryKey: String,
    val item: AthkarItem,
    val index: Int,
)
```

---

## 5. `sealed class`

A sealed class represents a limited set of possibilities. Think of it as: "this value can be one of these specific types, and nothing else."

```kotlin
// From BaseAthkarViewModel.kt
sealed class ViewEvent {
    data class NavigateTo(val route: String) : ViewEvent()
    data class ShowCompletion(val message: String) : ViewEvent()
    object NavigateBack : ViewEvent()
}
```

A `ViewEvent` is either `NavigateTo` (with a route string), `ShowCompletion` (with a message), or `NavigateBack` (no extra data). The compiler knows all possible types, so when you use `when` to handle them:

```kotlin
when (event) {
    is ViewEvent.NavigateTo -> navigate(event.route)
    is ViewEvent.ShowCompletion -> showSnackbar(event.message)
    is ViewEvent.NavigateBack -> goBack()
}
// No `else` needed — compiler knows you covered all cases
```

This is much safer than using strings or integers to represent different event types.

---

## 6. `companion object`

A `companion object` inside a class gives you static-like members — things that belong to the class itself, not to any instance.

```kotlin
// From AthkarixApp.kt
class AthkarixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AthkarixApp  // accessible as AthkarixApp.instance
            private set                     // only this class can set it
    }
}
```

And in `AssmaHussnaItem.kt`:

```kotlin
data class AssmaHussnaItem(...) {
    companion object {
        fun fromJson(json: JSONObject): AssmaHussnaItem = AssmaHussnaItem(
            id = json.getInt("id"),
            name = json.getString("name"),
            text = json.getString("text"),
        )
    }
}
// Call it: AssmaHussnaItem.fromJson(jsonObject)
```

---

## 7. Abstract Classes and Inheritance

An `abstract class` defines a template. Subclasses fill in the missing pieces.

```kotlin
// From BaseAthkarViewModel.kt — the template
abstract class BaseAthkarViewModel : ViewModel() {
    // Subclasses MUST provide these:
    abstract val maxPageCounters: List<Int>
    abstract val dataList: List<AthkarItem>
    abstract val completionMessage: String

    // Subclasses INHERIT this method (already implemented):
    fun incrementPageController() { ... }
    fun goToHome() { ... }
}

// From AthkarSabahViewModel.kt — one concrete implementation
class AthkarSabahViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarSabahList
    override val completionMessage: String = "أتممت أذكار الصباح"
    override val maxPageCounters: List<Int> = listOf(1, 1, 3, 1, ...)
}

// From AthkarMassaViewModel.kt — another concrete implementation
class AthkarMassaViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarMassaList
    override val completionMessage: String = "أتممت أذكار المساء"
    override val maxPageCounters: List<Int> = List(22) { 1 }
}
```

The pattern: 11 different athkar categories share the same logic (swiping, counter, completion), but each has its own data, messages, and tap requirements. The abstract base class avoids duplicating the shared logic 11 times.

---

## 8. Coroutines (`viewModelScope`, `launch`, `Dispatchers`)

Coroutines let you write asynchronous code (loading data, waiting, responding to events) that reads like normal sequential code.

```kotlin
// From AssmaHussnaViewModel.kt
fun loadData() {
    viewModelScope.launch(Dispatchers.IO) {   // run on background thread
        try {
            val data = AssmaHussnaService.getAllAssmaHussna(appContext)
            _dataList.value = data.map { item ->
                AthkarItem(duaText = "[${item.name}]\n\n${item.text}")
            }
            _isLoading.value = false           // update UI state
        } catch (e: Exception) {
            _hasError.value = true
            _errorMessage.value = e.message ?: "فشل تحميل البيانات"
        }
    }
}
```

What's happening:
- `viewModelScope` — a built-in coroutine scope tied to the ViewModel's lifecycle. If the ViewModel is destroyed, all coroutines in this scope are cancelled automatically.
- `launch { }` — starts a coroutine (like `Thread.start()` but cheaper)
- `Dispatchers.IO` — runs the coroutine on a thread pool for I/O (file/network operations). `Dispatchers.Main` is for UI updates.
- Inside `launch`, you can use regular `try/catch` — no callback hell.

```kotlin
// From HomeViewModel.kt
private fun navigate(route: String) {
    viewModelScope.launch {
        _navigationEvent.emit(HomeNavigationEvent.GoToRoute(route))
    }
}
```

`emit` on a `SharedFlow` is a suspend function — it must be called from a coroutine.

---

## 9. `StateFlow` and `SharedFlow`

These are Kotlin's reactive state containers — they hold a value and emit updates to anyone listening.

### StateFlow — for continuous state

```kotlin
// From FontViewModel.kt
class FontViewModel : ViewModel() {
    private val _fontSize = MutableStateFlow(28.6f)  // private mutable version
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()  // public read-only version

    fun increaseFontSize() {
        if (_fontSize.value < maxFontSize) {
            _fontSize.value += 2.0f
        }
    }
}
```

- `StateFlow` always has a current value (28.6f initially)
- `asStateFlow()` creates a read-only snapshot for external code
- In the Compose UI, you collect it: `val fontSize by fontViewModel.fontSize.collectAsState()`
- Every time the value changes, the Composable automatically recomposes

### SharedFlow — for one-shot events

```kotlin
// From BaseAthkarViewModel.kt
private val _eventFlow = MutableSharedFlow<ViewEvent>()
val eventFlow: SharedFlow<ViewEvent> = _eventFlow.asSharedFlow()

fun incrementPageController() {
    // ... when last page is completed:
    viewModelScope.launch {
        _eventFlow.emit(ViewEvent.ShowCompletion(completionMessage))
    }
}
```

- `SharedFlow` does NOT have a current value — it emits events that are consumed once
- Perfect for: navigation commands, snackbar messages, haptic feedback
- Collected in Compose via `LaunchedEffect`: collects events and reacts to each one

### The Pattern

```
StateFlow  → "the page index is 3 right now"  (continuous, has "now" value)
SharedFlow → "navigate to settings"            (one-shot, no "now" value)
```

---

## 10. Null Safety: `?`, `?.`, `!!`, `lateinit`

```kotlin
// From AthkarItem.kt — ? means the value CAN be null
data class AthkarItem(
    val duaText: String?,      // two possibilities: a String, or null
    val footer: String? = null, // defaults to null
)
```

```kotlin
// Safe call ?. — only proceed if NOT null
dataList.getOrNull(index)?.duaText  // returns null if index is out of bounds or duaText is null
```

```kotlin
// Non-null assertion !! — "I PROMISE this is not null" (dangerous if you're wrong)
fun provideFontViewModel(): FontViewModel {
    if (fontViewModel == null) {
        fontViewModel = FontViewModel()
    }
    return fontViewModel!!  // crash if fontViewModel is somehow still null
}
```

```kotlin
// lateinit — "I will set this before it's used, trust me"
class AthkarixApp : Application() {
    companion object {
        lateinit var instance: AthkarixApp  // initialized in onCreate(), not in constructor
    }
}
```

Rules of thumb:
- Prefer `?.` over `!!` — safe calls never crash
- Use `!!` only when you're absolutely certain (like after null-check + assignment)
- Use `lateinit` for dependencies set after construction

---

## 11. `when` Expressions

`when` is Kotlin's more powerful version of `switch`. It can match types, values, and conditions.

```kotlin
// From AssmaHussnaScreen.kt — exhaustive when with 3 branches
when {
    isLoading -> CircularProgressIndicator(...)
    hasError -> Column { Text(errorMsg); Button("إعادة المحاولة") }
    else -> AthkarTextSlider(...)
}
```

With sealed classes, `when` becomes even more powerful:

```kotlin
when (event) {
    is ViewEvent.NavigateTo -> handleNavigation(event.route)
    is ViewEvent.ShowCompletion -> showSnackbar(event.message)
    is ViewEvent.NavigateBack -> handleBack()
    // No else needed — the compiler knows these are ALL the possibilities
}
```

---

## 12. `remember` and `LaunchedEffect` (Compose)

These are Compose-specific Kotlin functions for managing state and side effects.

### `remember`

Keeps a value across recompositions. Without `remember`, the ViewModel would be recreated on every frame.

```kotlin
// From AthkarixNavGraph.kt
composable(Routes.HOME) {
    val vm = remember { AppModule.provideHomeViewModel() }
    // vm is created once and remembered — survives recompositions
    HomeScreen(viewModel = vm, ...)
}
```

### `LaunchedEffect`

Runs a coroutine when the Composable enters the composition. Perfect for collecting SharedFlows and one-time work.

```kotlin
// From AssmaHussnaScreen.kt
LaunchedEffect(Unit) { viewModel.loadData() }
// Runs once when the screen appears. Unit means "don't re-run on recomposition."
```

```kotlin
// From AthkarTextSlider.kt
LaunchedEffect(pageIndex) {
    if (pagerState.currentPage != pageIndex) {
        pagerState.animateScrollToPage(pageIndex)
    }
}
// Re-runs whenever pageIndex changes — syncs the pager with the ViewModel
```

---

## Summary: Kotlin Features by Usage Frequency

| Feature | Used where? | How many times? |
|---------|-------------|-----------------|
| `val`/`var` | Every file | Hundreds |
| `fun` | Every file | Hundreds |
| `object` | Singletons (8 files) | 8 |
| `data class` | Models | 6+ |
| `sealed class` | Events | 2 (ViewEvent, HomeNavigationEvent) |
| `companion object` | Static members | 2 (AthkarixApp, AssmaHussnaItem) |
| `abstract class` | ViewModel base | 1 (BaseAthkarViewModel + 11 subclasses) |
| Coroutines | ViewModels | ~10 files |
| `StateFlow` | All ViewModels | ~15 files |
| `SharedFlow` | Event buses | 3 files |
| Null safety (`?.`) | Throughout | ~30+ places |
| `when` | Screen composables | ~5 places |
| `remember` | NavGraph, screens | ~15 places |
| `LaunchedEffect` | Side effects | ~5 places |
