# ViewModel Deep Dive

## What Is a ViewModel?

A `ViewModel` is a class that holds UI data and survives configuration changes (like screen rotation). Without a ViewModel, rotating your phone would destroy all your state — the counter would reset, the current page would go back to 0, etc.

In Athkarix, ViewModels are the central hub that connects the data layer to the UI. They expose state as `StateFlow` and one-shot events as `SharedFlow`.

---

## BaseAthkarViewModel — The Core Brain

**File**: `viewmodel/BaseAthkarViewModel.kt` (80 lines)

This abstract class powers ALL 11 athkar categories plus the 99 Names screen. Let's understand every line.

### What subclasses must provide:

```kotlin
abstract class BaseAthkarViewModel : ViewModel() {
    abstract val maxPageCounters: List<Int>
    abstract val dataList: List<AthkarItem>
    abstract val completionMessage: String
```

Three things each category must define:
- `dataList` — what athkar to show (the actual content)
- `maxPageCounters` — how many times to tap each page before advancing
- `completionMessage` — what to show when the user finishes all pages

### State the ViewModel holds:

```kotlin
private val _currentPageIndex = MutableStateFlow(0)
val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

private val _currentPageCounter = MutableStateFlow(0)
val currentPageCounter: StateFlow<Int> = _currentPageCounter.asStateFlow()
```

Two pieces of state:
1. **Which page** the user is on (0 = first page)
2. **How many times** they've tapped the current page

The naming convention `_name` (private mutable) / `name` (public read-only) is standard Kotlin. The underscore version can be changed internally; the public version only exposes reading.

### Events the ViewModel emits:

```kotlin
private val _eventFlow = MutableSharedFlow<ViewEvent>()
val eventFlow: SharedFlow<ViewEvent> = _eventFlow.asSharedFlow()

private val _hapticTrigger = MutableSharedFlow<Unit>()
val hapticTrigger: SharedFlow<Unit> = _hapticTrigger.asSharedFlow()
```

Two event channels:
1. `eventFlow` — navigation commands and completion messages
2. `hapticTrigger` — buzz the phone when advancing pages

### The Core Logic: `incrementPageController()`

```kotlin
fun incrementPageController() {
    val max = maxPageCounters.getOrElse(_currentPageIndex.value) { 1 }
    val newCount = _currentPageCounter.value + 1
    if (newCount >= max) {
        // We've tapped enough — move to next page
        _currentPageCounter.value = 0
        val nextIndex = _currentPageIndex.value + 1
        if (nextIndex < dataList.size) {
            // There's a next page — go to it
            _currentPageIndex.value = nextIndex
            viewModelScope.launch { _hapticTrigger.emit(Unit) }
        } else {
            // That was the last page — show completion
            viewModelScope.launch {
                _eventFlow.emit(ViewEvent.ShowCompletion(completionMessage))
            }
        }
    } else {
        // Haven't tapped enough yet — just increment counter
        _currentPageCounter.value = newCount
    }
}
```

Let's trace an example. For Morning Athkar, page 8 (index 7) has `maxPageCounters[7] = 4`:

```
Tap #1: newCount=1,  1 < 4 → counter becomes 1, stay on page 7
Tap #2: newCount=2,  2 < 4 → counter becomes 2, stay on page 7
Tap #3: newCount=3,  3 < 4 → counter becomes 3, stay on page 7
Tap #4: newCount=4,  4 >= 4 → counter = 0, advance to page 8, haptic buzz
```

For categories where `maxPageCounters` is all 1s (most categories), every single tap advances to the next page.

### Other Methods

```kotlin
fun onPageChanged(index: Int) {
    _currentPageIndex.value = index
    resetCounter()     // user swiped — reset the tap counter
}

fun goToPage(index: Int) {
    _currentPageIndex.value = index
    _currentPageCounter.value = 0
}

fun goToHome() {
    viewModelScope.launch {
        _eventFlow.emit(ViewEvent.NavigateTo("home"))
    }
}

fun getShareText(index: Int): String {
    return dataList.getOrNull(index)?.duaText ?: ""
}
```

### ViewEvent Sealed Class

```kotlin
sealed class ViewEvent {
    data class NavigateTo(val route: String) : ViewEvent()
    data class ShowCompletion(val message: String) : ViewEvent()
    object NavigateBack : ViewEvent()
}
```

Three event types, each with different associated data. `NavigateTo` needs a route, `ShowCompletion` needs a message, and `NavigateBack` needs nothing.

---

## The 11 Athkar ViewModels

Every athkar category has its own ViewModel. Most are trivially simple because `BaseAthkarViewModel` does all the work.

### Simple Case — AthkarMassaViewModel (evening athkar)

```kotlin
class AthkarMassaViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarMassaList
    override val maxPageCounters: List<Int> get() = List(22) { 1 }
    override val completionMessage: String = "أتممت أذكار المساء"
}
```

#### Alternative form for line 145 — explicit `get()` with block body

Line 145 uses Kotlin's **expression-body** getter syntax (`get() = value`). The same property can be written with an explicit **block body** using `{}` braces and a `return` statement:

```kotlin
class AthkarMassaViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem>
        get() {
            return AthkarRepository.athkarMassaList
        }

    override val maxPageCounters: List<Int>
        get() {
            return List(22) { 1 }
        }

    override val completionMessage: String = "أتممت أذكار المساء"
}
```

When to use which form:

| Form | Syntax | Use when |
|------|--------|----------|
| Expression body | `get() = expr` | The getter returns a single expression directly |
| Block body | `get() { return expr }` | You need multiple statements, logging, validation, or want the extra readability |

Both forms behave identically at runtime — the compiler lowers the expression form into a block body internally. The block form becomes necessary only when you need to execute extra logic (e.g., logging, conditional return, or computing from multiple steps).

- 22 items, each requiring only 1 tap before advancing
- Completion message: "I completed the evening athkar"

### Complex Case — AthkarSabahViewModel (morning athkar)

```kotlin
class AthkarSabahViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarSabahList

    override val maxPageCounters: List<Int> = listOf(
        1, 1, 3, 1, 1, 1, 4, 1, 3, 7, 1, 1, 3, 3, 1, 1, 1, 100, 10, 100, 3, 1, 100, 10
    )

    override val completionMessage: String = "أتممت أذكار الصباح"
}
```

Some morning athkar require **100 taps** before advancing (e.g., index 17: "سبحان الله وبحمده" 100 times). This is the only category with custom counter requirements.

### The Full List

| ViewModel | Items | Custom counters? | Completion message |
|-----------|-------|-----------------|-------------------|
| AthkarSabahViewModel | 24 | Yes (3, 7, 100, etc.) | "أتممت أذكار الصباح" |
| AthkarMassaViewModel | 22 | All 1s | "أتممت أذكار المساء" |
| AthkarAfterSalatViewModel | 11 | All 1s | "أتممت أذكار ما بعد الصلاة" |
| AthkarBeforeBedViewModel | 9 | All 1s | "أتممت أذكار النوم" |
| TasbihViewModel | 35 | All 1s | "أتممت التسبيح" |
| EstigfarViewModel | 25 | All 1s | "أتممت الاستغفار" |
| HamdViewModel | 57 | All 1s | "أتممت الحمد" |
| SalatAlaRasoulViewModel | 44 | All 1s | "أتممت الصلاة على الرسول" |
| DuaMenQuranViewModel | 12 | All 1s | "أتممت دعاء من القرآن" |
| DuaMenSunnahViewModel | 43 | All 1s | "أتممت دعاء من السنة" |

---

## AssmaHussnaViewModel — Loading State Pattern

**File**: `viewmodel/AssmaHussnaViewModel.kt` (58 lines)

This ViewModel is special because it loads data from a JSON file (not from text constants). It demonstrates the **loading/error/content** pattern.

```kotlin
class AssmaHussnaViewModel(
    private val appContext: Context
) : BaseAthkarViewModel() {

    // Three state flows for loading status
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadData() {
        _isLoading.value = true
        _hasError.value = false
        viewModelScope.launch(Dispatchers.IO) {  // background thread
            try {
                val data = AssmaHussnaService.getAllAssmaHussna(appContext)
                _dataList.value = data.map { item ->
                    AthkarItem(duaText = "[${item.name}]\n\n${item.text}")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _hasError.value = true
                _errorMessage.value = e.message ?: "فشل تحميل البيانات"
            }
        }
    }
}
```

The three-state pattern:
- `isLoading = true` → show a spinning indicator
- `hasError = true` → show the error + retry button
- Otherwise → show the content (`AthkarTextSlider`)

The screen (`AssmaHussnaScreen.kt`) uses a `when` block:

```kotlin
when {
    isLoading -> CircularProgressIndicator(...)
    hasError -> Column { Text(errorMsg); Button(onClick = { viewModel.loadData() }) }
    else -> AthkarTextSlider(viewModel, fontViewModel)
}
```

---

## Other ViewModels

### FontViewModel
```kotlin
class FontViewModel : ViewModel() {
    private val _fontSize = MutableStateFlow(28.6f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _selectedFont = MutableStateFlow("Amiri")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    fun increaseFontSize() { if (_fontSize.value < 37f) _fontSize.value += 2f }
    fun decreaseFontSize() { if (_fontSize.value > 21f) _fontSize.value -= 2f }
    fun changeFont(font: String) { _selectedFont.value = font }
}
```

Simple state holder with bounds checking. Shared across all athkar screens.

#### Deep Dive: `MutableStateFlow` vs `StateFlow` vs `asStateFlow()`

This three-piece pattern appears in **every** ViewModel. Let's break it apart line by line.

**Step 1 — The mutable storage (private):**
```kotlin
private val _fontSize = MutableStateFlow(28.6f)
```
* `MutableStateFlow` is a **writable** container that holds a value *and* broadcasts it to anyone listening.
* `private` means **only the ViewModel itself** can change `_fontSize.value`. The UI cannot reach in and modify it from the outside.
* The leading underscore `_` is a Kotlin naming convention that says: "This is the private/internal version of a public property."

**Step 2 — The read-only exposure (public):**
```kotlin
val fontSize: StateFlow<Float> = _fontSize.asStateFlow()
```
* `.asStateFlow()` converts the mutable version into a **read-only** `StateFlow`.
* This is what the UI sees — it can *read* the current value and *observe* changes, but it **cannot** assign a new value to it.

**Step 3 — The UI listens (Compose side):**
```kotlin
// In some Composable
val size by viewModel.fontSize.collectAsState()    // rebuilds whenever _fontSize changes
```

> [!TIP]
> **Why split into two flows instead of one?**
> It enforces **encapsulation**. The ViewModel is the *only* place that decides *when* and *how* the font size changes (through `increaseFontSize()`, `decreaseFontSize()`). The UI cannot bypass that and just do `viewModel._fontSize.value = 999f`. This prevents bugs and keeps the rules (e.g. bounds checking) in one place.

> [!TIP]
> **Why "State" in `StateFlow`?**
> Unlike a normal `Flow` (which is a *stream* of values over time), a `StateFlow` always carries a **current state** — it has a `.value` you can read at any moment, even with no subscribers. The UI can read `viewModel.fontSize.value` synchronously on the very first render, before any new emission happens.

```kotlin
viewModel.fontSize.value          // 👈 read the current size right now (e.g., 28.6f)
collectAsState()                  // 👈 also subscribe to future changes
```

| Property | `MutableStateFlow<T>` | `StateFlow<T>` (after `.asStateFlow()`) |
|----------|------------------------|------------------------------------------|
| Can read `.value`? | ✅ Yes | ✅ Yes |
| Can assign `.value = ...`? | ✅ Yes | ❌ No (read-only) |
| Can `emit()` new values? | ✅ Yes | ❌ No |
| Who can use it? | Only inside the ViewModel (because of `private`) | Anyone (UI, other ViewModels, tests) |
| Purpose | The **backing storage** | The **public read window** |

> [!IMPORTANT]
> **Beginner trap:** Writing `val fontSize = MutableStateFlow(28.6f)` (without the underscore split) compiles and works, but **breaks encapsulation** — now any screen or test can do `viewModel.fontSize.value = 999f`, skipping your bounds-checking methods. Always split into private-mutable + public-readonly.

### FloatingCounterViewModel
```kotlin
class FloatingCounterViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun increment() { _counter.value++ }
    fun incrementUntil100() { _counter.value = (_counter.value + 1) % 100 }
    fun reset() { _counter.value = 0 }
}
```

A simple 0→99→0 counter for tasbeeh. Shared across Tasbih, Estigfar, Hamd, and Salat screens.

### NotificationSettingsViewModel
```kotlin
class NotificationSettingsViewModel(
    private val prefsManager: SharedPrefsManager,
    private val notificationService: NotificationService,
) : ViewModel() {
    // Exposes morningEnabled, eveningEnabled, morningHour, etc. as StateFlows
    // Methods toggle preferences and schedule/cancel alarms
}
```

Wraps `SharedPrefsManager` + `NotificationService` into reactive state.

### HomeViewModel

```kotlin
class HomeViewModel : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent: SharedFlow<HomeNavigationEvent> = _navigationEvent.asSharedFlow()

    fun goToAthkarSabah() = navigate("athkar_sabah")
    fun goToAthkarMassa() = navigate("athkar_massa")
    // ... 10 more navigation methods

    private fun navigate(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.GoToRoute(route))
        }
    }
}
```

Uses `SharedFlow` because navigation is a one-shot event — not something you want to re-emit on rotation.

#### Deep Dive: The Navigation Method Pattern

Let's unpack each part of `HomeViewModel` and the reason it is shaped this way.

**1. Why each screen has its own `goTo...()` method**

```kotlin
fun goToAthkarSabah() = navigate("athkar_sabah")
fun goToAthkarMassa() = navigate("athkar_massa")
// ... 10 more
```

At first glance this looks repetitive — why not just expose `navigate(route: String)` to the UI and let it pass any string? Three reasons:

* **Type safety.** The UI can only call `goToAthkarSabah()` — it cannot pass a typo like `"athkar_sabha"` that would silently navigate to a non-existent route.
* **Discoverability & autocomplete.** In Android Studio, typing `viewModel.goTo` shows all available destinations in a popup. Passing raw strings gives you no help.
* **Single point of change.** If the route `"athkar_sabah"` ever changes (e.g., to `"home/athkar/sabah"`), you update **one line** in the ViewModel, not every screen that navigates to it.

**2. The `fun x() = expr` single-expression syntax**

```kotlin
fun goToAthkarSabah() = navigate("athkar_sabah")
```

This is Kotlin's **single-expression function** form — equivalent to:

```kotlin
fun goToAthkarSabah() {
    return navigate("athkar_sabah")
}
```

It's used here because each method does *exactly one thing*: delegate to `navigate()`. No branching, no logic — just a forwarder. The `=` syntax makes that intent obvious at a glance.

> [!TIP]
> **Rule of thumb:** If the function body is a *single expression* and you can read it left-to-right (`goToAthkarSabah = navigate("athkar_sabah")`), use `=`. If it has multiple statements, conditions, or side-effects, use the block form `{ }`.

**3. The private `navigate()` helper**

```kotlin
private fun navigate(route: String) {
    viewModelScope.launch {
        _navigationEvent.emit(HomeNavigationEvent.GoToRoute(route))
    }
}
```

This is the **only** place that touches `_navigationEvent` and the **only** place that launches a coroutine for navigation. Every public `goTo...()` method funnels through it. This gives us:

* **One place to add cross-cutting concerns later** — analytics, logging, debouncing, etc. — without touching 11 public methods.
* **Encapsulation** — the UI cannot emit raw events; it must go through a public method.
* **Consistency** — every navigation is launched the same way, in the same coroutine scope.

**4. Why `SharedFlow` (and not `StateFlow`) for navigation?**

StateFlow's defining feature is that it always carries a **current value**. If we used `StateFlow` here:

```kotlin
// ❌ Hypothetical — DON'T do this
private val _navigationEvent = MutableStateFlow<HomeNavigationEvent?>(null)
val navigationEvent: StateFlow<HomeNavigationEvent?> = _navigationEvent.asStateFlow()
```

Then the moment the user **rotates the phone**, Compose re-collects the flow and *re-navigates* to the last route — because the StateFlow still holds the old event as its "current value"! This would either crash the back stack or trap the user in a loop.

`SharedFlow` has **no current value**. It is a *pure signal* — emit it, deliver it, forget it. If no one is listening when you emit, the event is lost. That's exactly what we want for one-shot side effects like navigation, snackbars, or haptic feedback.

| Concern | `StateFlow` | `SharedFlow` |
|---------|-------------|--------------|
| Carries "current value"? | ✅ Yes (`.value`) | ❌ No |
| Re-delivers last value to new collectors? | ✅ Yes (the latest) | ❌ No (only what arrives after they subscribe) |
| Good for | UI state that should always render correctly (page index, font size, results) | One-shot events (navigate, toast, vibrate, completion) |
| In Athkarix | `currentPageIndex`, `fontSize`, `query`, `results` | `navigationEvent`, `eventFlow`, `hapticTrigger` |

> [!IMPORTANT]
> **Beginner trap:** When in doubt, don't reach for `StateFlow` by default. Ask: *"If the user rotates the phone, should this happen again?"* If **no** → `SharedFlow`. If **yes** (it's part of the screen's current state) → `StateFlow`.

### SearchViewModel

```kotlin
class SearchViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results: StateFlow<List<SearchResult>> = _results.asStateFlow()

    fun search(q: String) {
        _query.value = q
        // Iterates all categories, normalizes Arabic diacritics, filters matches
    }
}
```

Full-text search is entirely client-side. It iterates all 10 athkar categories in `AthkarRepository`, strips Arabic diacritics with `DiacriticUtil.remove()`, and checks `String.contains()`.

---

## Summary: ViewModel Patterns

| Pattern | Description | Examples |
|---------|-------------|---------|
| **Abstract base** | Shared logic in base, data in subclasses | BaseAthkarViewModel + 11 subclasses |
| **Loading/Error/Content** | Three-state lifecycle for async data | AssmaHussnaViewModel |
| **Simple state holder** | Just exposes state with mutation methods | FontViewModel, FloatingCounterViewModel |
| **Event emitter** | One-shot events via SharedFlow | HomeViewModel, BaseAthkarViewModel |
| **Wrapper** | Wraps another service in reactive state | NotificationSettingsViewModel |
