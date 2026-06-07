# 🚀 Kotlin Concepts Masterclass: From Beginner to Pro

Welcome! This guide is designed to take you on a progressive journey through Kotlin, specifically tailored to how we use it in the **Athkarix** Android app. 

Whether you are a Junior learning the ropes, or a Senior from another language (like Java or Dart) getting comfortable with Kotlin, this guide is built for **you**. 

We’ve divided the concepts into **4 Progressive Phases**. Let's dive in! ☕️

---

## 🟢 Phase 1: The Absolute Basics
*The foundation of every Kotlin file you will ever write.*

### 1. `val` vs `var` (Immutability by Default)
Kotlin loves safety. That means we prefer things that don't change.
* **`val` (Value):** Read-only. Once assigned, you can never change what it points to (like `final` in Java or `const` in JS).
* **`var` (Variable):** Mutable. You can reassign it.

```kotlin
// In AthkarItem.kt
data class AthkarItem(
    val duaText: String?,   // ✅ Set once, never changes
    var readCount: Int = 0  // ⚠️ Can be changed later
)
```

### 2. Functions (`fun`)
Functions in Kotlin are clean and concise. Types come *after* the variable names.

```kotlin
// In DiacriticUtil.kt
fun removeDiacritics(text: String): String {
    return text.replace(Regex("[\u064B-\u065F\u0670]"), "")
}

// 🔥 Pro-tip: Single-expression functions
fun increment(counter: Int) = counter + 1
```

### 3. Null Safety (`?`, `?.`, `!!`)
Kotlin eliminates the dreaded `NullPointerException` by forcing you to declare if something *can* be null.

> [!TIP]
> If a type has a `?` at the end, it means "This might be null. Handle it carefully!"

```kotlin
val duaText: String? = null // Allowed!

// ✅ Safe Call (?.) - Only runs if duaText is NOT null
val length = duaText?.length 

// 🚨 Danger (!!) - "Trust me, it's not null." (Will crash if you're wrong)
val crashLength = duaText!!.length 
```

### 4. The Almighty `when` Expression
Forget `switch` statements. `when` is smarter, safer, and can return values.

```kotlin
// In AssmaHussnaScreen.kt
when {
    isLoading -> CircularProgressIndicator()
    hasError -> Text("Error loading data")
    else -> AthkarTextSlider()
}
```

---

## 🟡 Phase 2: Object-Oriented Kotlin
*How we structure data and logic in Athkarix.*

### 5. `data class` (Data Holders)
Need a class just to hold data? Don't write getters, setters, or `toString()`. Kotlin does it for you.

```kotlin
// Gives you equals(), hashCode(), toString(), and copy() automatically!
data class SearchResult(
    val category: String,
    val item: AthkarItem
)

// Making a modified copy is trivial:
val result2 = result1.copy(category = "New Category")
```

### 6. `sealed class` (Restricted Hierarchies)

هو عبارة عن `enum` لكنه مطور، ويستخدم غالباً مع `when` / `switch`. الفكرة أنه يمثّل **مجموعة مغلقة ومحدودة** من الحالات، لكن — على عكس `enum` — كل حالة (subclass) تستطيع أن تحمل **بياناتها الخاصة المختلفة** عن باقي الحالات.

```kotlin
sealed class DeliveryStatus {
    data class OnTheWay(val driverName: String, val minutesLeft: Int) : DeliveryStatus()
    data class Delivered(val timeStamp: String) : DeliveryStatus()
    data class Canceled(val reason: String) : DeliveryStatus()
}

fun checkStatus(status: DeliveryStatus) {
    when (status) {
        is DeliveryStatus.OnTheWay -> println("السائق ${status.driverName} سيصل خلال ${status.minutesLeft} دقيقة.")
        is DeliveryStatus.Delivered -> println("تم التوصيل بنجاح في: ${status.timeStamp}")
        is DeliveryStatus.Canceled  -> println("للأسف تم إلغاء الطلب بسبب: ${status.reason}")
        // لاحظ: لم نكتب else!! الكود آمن 100% لأن الاحتمالات مغلقة ومضمونة.
    }
}
```

*Why use it?* When you use a `when` statement on a sealed class, the compiler **forces** you to handle every case. No bugs!

A `sealed class` is like an `enum` on steroids. It represents a strict, limited set of possibilities, but each possibility can hold its own unique data!

```kotlin
// In BaseAthkarViewModel.kt
sealed class ViewEvent {
    data class NavigateTo(val route: String) : ViewEvent()
    data class ShowCompletion(val message: String) : ViewEvent()
    object NavigateBack : ViewEvent()
}
```

### 7. `object` (The Singleton)
Whenever you need exactly *one* instance of something globally, use `object` instead of `class`. No `new` keywords required.

```kotlin
// A generic teaching example — production code in this project
// lives in `ui/theme/AppColor.kt`; see 05-ui-layer.md § Theme.
object AppPalette {
    val primaryGold = 0xFFFFD700
    val background = 0xFF000000
}
// Usage: AppPalette.primaryGold
```

#### `class` vs `object` — What's the Difference?

Think of it like a **blueprint** vs a **single physical thing**.

* **`class` = Blueprint.** A `class` is just a *plan* or *template*. It does nothing by itself — you must **build (instantiate)** it to use it. You can build as many copies as you want.
* **`object` = The Thing Itself.** An `object` is the *real, one-of-a-kind thing* — it is created automatically the moment your program runs. You can never build another one.

```kotlin
// 🟦 class — a BLUEPRINT. Calling it directly does nothing.
class Car {
    fun drive() = println("Driving...")
}

// You MUST create an instance with the blueprint:
val myCar = Car()
val yourCar = Car()   // ✅ You can have many cars!
myCar.drive()

// 🟨 object — the REAL thing. Already exists. No creation needed.
object God {
    fun isOne() = println("There is no god but Allah")
}

// You DON'T create it — it's already there:
God.isOne()           // ✅ Always works. Only ONE God exists.
```

> [!TIP]
> **Rule of thumb (the 3 questions):**
> 1. Do I need **many copies** of this thing? → Use `class`
> 2. Will there be **only one** of this thing, ever? → Use `object`
> 3. Am I storing **per-user/per-screen data**? → Use `class` (each instance carries its own data)

```kotlin
// ✅ Use class — many Athkar items, each with its own count
class AthkarItem(val text: String) {
    var readCount: Int = 0   // each item has its own count
}

// ✅ Use object — only one set of app colors in the whole app
object AppColor {
    val primaryGold = Color(0xFFFFD700)
}
```

| Question | `class` | `object` |
|----------|---------|----------|
| How many exist? | As many as you create | Exactly **one** (the singleton) |
| Need to use `val x = ...`? | ✅ Yes, you instantiate it | ❌ No, it's already there |
| Holds per-instance data? | ✅ Yes (e.g. `readCount`) | ❌ No, shared globally |
| Analogy | A **cookie cutter** (makes many cookies) | The **sun** (only one exists) |
| Lifetime | Lives as long as your variable holds it | Lives for the whole app |

> [!IMPORTANT]
> **Beginner trap:** A common mistake is writing `class AppColor { ... }` and then calling `AppColor.primaryGold` directly — this **will not compile** in Kotlin, because a `class` must be instantiated first. If you never need more than one, use `object`!

### 8. `companion object` (Statics)
Kotlin doesn't have `static` keywords. Instead, we use `companion object` inside a class for things that belong to the class itself, not the instance.

```kotlin
class AssmaHussnaItem(...) {
    companion object {
        fun fromJson(json: JSONObject): AssmaHussnaItem = ...
    }
}
// Usage: AssmaHussnaItem.fromJson(...)
}
```

### 9. Function Overloading (Same Name, Different Signatures)
Overloading means defining **multiple functions with the same name** in the same scope, as long as their **parameter list differs** (different number of parameters, or different types). The compiler picks the right one based on what you pass in. Return type alone is *not* enough to distinguish overloads.

```kotlin
// In SearchViewModel.kt — same name, different signatures
fun search(query: String): List<AthkarItem> {
    return repository.searchAll(query)
}

fun search(query: String, category: String): List<AthkarItem> {
    return repository.searchAll(query).filter { it.category == category }
}

fun search(query: String, maxResults: Int): List<AthkarItem> {
    return repository.searchAll(query).take(maxResults)
}

// Usage — the compiler routes to the correct overload automatically:
search("سبحان")                          // → calls the 1-arg version
search("سبحان", category = "morning")    // → calls the 2-arg version
search("سبحان", maxResults = 10)         // → calls the 2-arg version
```

> [!TIP]
> **Why overload instead of default parameters?** Both work, but overloading shines when the parameter types differ (e.g., `search(String)` vs `search(AthkarItem)`) — default parameters can't do that.

### 10. Function Overriding (Subclass Replaces Parent Behavior)
Overriding is when a **subclass provides its own implementation** of a function that is already declared (open/abstract) in its parent class. The subclass version is marked with `override`. This is the heart of polymorphism.

```kotlin
// In BaseAthkarViewModel.kt — the parent defines the *contract*
abstract class BaseAthkarViewModel : ViewModel() {
    abstract val dataList: List<AthkarItem>
    abstract val maxPageCounters: List<Int>
    abstract val completionMessage: String

    // Shared logic that all subclasses inherit as-is
    fun incrementPageController() { /* ... */ }
}

// In AthkarSabahViewModel.kt — the subclass fills in the contract
class AthkarSabahViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> = AthkarRepository.athkarSabahList
    override val maxPageCounters: List<Int> = listOf(1, 1, 3, 1, /* ... */ 10)
    override val completionMessage: String = "أنهيت أذكار الصباح !"
}
```

> [!IMPORTANT]
> **Overloading vs Overriding — don't mix them up!**
>
> | Aspect | Overloading | Overriding |
> |--------|-------------|------------|
> | Where? | Same class | Parent → child class |
> | Signature | Must be **different** | Must be **identical** |
> | Keyword | None | `override` (required) |
> | Purpose | Multiple ways to call the same operation | Subclass customizes parent behavior |
> | Decision time | Compile-time (which overload?) | Runtime (which override?) |

---
## 🟠 Phase 3: The Asynchronous World
*Handling background work and UI states without freezing the app.*

### 9. Coroutines (`viewModelScope`, `launch`)
Coroutines are Kotlin's lightweight threads. They make asynchronous code (like fetching JSON or saving data) read like normal, sequential code.

```kotlin
fun loadData() {
    // Launches a coroutine tied to the ViewModel's lifecycle
    viewModelScope.launch(Dispatchers.IO) {
        val data = AssmaHussnaService.getAll() // Runs in background!
        _dataList.value = data                 // Updates UI
    }
}
```

### 10. `StateFlow` vs `MutableStateFlow`
A one-paragraph pointer is enough at this stage — the **full deep dive** (private-mutable / public-readonly split, `collectAsState()`, why-`StateFlow`-not-`var`, Flutter GetX `.obs` comparison table) lives in **[`04-viewmodel-deep-dive.md`](./04-viewmodel-deep-dive.md)** under *Deep Dive: MutableStateFlow vs StateFlow vs asStateFlow()*. Read it once you reach the ViewModel chapter.

The mental model in one sentence: **`StateFlow` is a radio station — the UI subscribes once and rebuilds whenever the value changes.**

> [!TIP]
> **Coming from Flutter (GetX)?** `MutableStateFlow` is the equivalent of `.obs`; `collectAsState()` is the equivalent of `Obx(() => …)`. The full side-by-side table is in `04`.

---
## 🔴 Phase 4: Functional & Compose Magic
*Advanced tricks that make Kotlin incredibly expressive.*

### 11. Higher-Order Functions & Lambdas
A function that accepts *another function* as a parameter. This is how Jetpack Compose handles all button clicks and events!

```kotlin
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) { ... }

// Usage: The code inside { } is a lambda function!
PrimaryButton(text = "التالي") {
    viewModel.goToNextPage()
}
```

### 12. Scope Functions (`let`, `apply`)
Run code blocks within the context of an object to keep code extremely clean.

* **`let` (For Null Safety):**
```kotlin
// Only runs the block if duaText is NOT null. 'it' represents the text.
athkarItem.duaText?.let {
    println("Dua: $it")
}
```

* **`apply` (For Object Configuration):**
```kotlin
// Configure the intent immediately after creating it
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "نص الدعاء")
}
```

### 13. Extension Functions
Want to add a new method to the built-in `String` or `Context` classes without modifying their source code?

```kotlin
fun String.toArabicNumerals(): String {
    return this.replace("1", "١").replace("2", "٢") //...
}

// Now EVERY string in your app can do this!
val translated = "Page 1".toArabicNumerals()
```

### 14. Compose Effects (`remember`, `LaunchedEffect`)
For the full story on how Compose remembers state and runs side effects (including how `remember { AppModule.provideHomeViewModel() }` ties into the manual DI graph), see **[`05-ui-layer.md`](./05-ui-layer.md)** § *Key Components* and **[`07-navigation-and-di.md`](./07-navigation-and-di.md)** § *How NavGraph Uses AppModule*.

The mental model in one sentence each:

* **`remember`** — "Keep this value across recompositions of the same screen."
* **`LaunchedEffect(key)`** — "Run this coroutine once, and re-run it if `key` changes."

---

## 🔴 Phase 4: Functional & Compose Magic
*Advanced tricks that make Kotlin incredibly expressive.*

### 11. Higher-Order Functions & Lambdas
A function that accepts *another function* as a parameter. This is how Jetpack Compose handles all button clicks and events!

```kotlin
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) { ... }

// Usage: The code inside { } is a lambda function!
PrimaryButton(text = "التالي") { 
    viewModel.goToNextPage() 
}
```

### 12. Scope Functions (`let`, `apply`)
Run code blocks within the context of an object to keep code extremely clean.

* **`let` (For Null Safety):**
```kotlin
// Only runs the block if duaText is NOT null. 'it' represents the text.
athkarItem.duaText?.let { 
    println("Dua: $it") 
}
```

* **`apply` (For Object Configuration):**
```kotlin
// Configure the intent immediately after creating it
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "نص الدعاء")
}
```

### 13. Extension Functions
Want to add a new method to the built-in `String` or `Context` classes without modifying their source code?

```kotlin
fun String.toArabicNumerals(): String {
    return this.replace("1", "١").replace("2", "٢") //...
}

// Now EVERY string in your app can do this!
val translated = "Page 1".toArabicNumerals() 
```

### 14. Compose Effects (`remember`, `LaunchedEffect`)
When working in Compose UI, these two are your best friends:

* **`remember`**: Tells Compose, "Hey, remember this value even if you redraw the screen."
```kotlin
val viewModel = remember { AppModule.provideHomeViewModel() }
```

* **`LaunchedEffect`**: Tells Compose, "Hey, run this background coroutine exactly *once* when this screen opens."
```kotlin
LaunchedEffect(Unit) { 
    viewModel.loadData() 
}
```

---

## 📊 Quick Cheatsheet

| Concept | Use Case | Example |
|---------|----------|---------|
| `val` | Constant reference | `val name = "App"` |
| `data class` | Holding data | `data class User(val id: Int)` |
| `object` | Singletons | `object Analytics { }` |
| `sealed class` | Strict UI States | `sealed class UiState` |
| `StateFlow` | Reactive UI State | `val text = MutableStateFlow("")` |
| `let` | Safe null-unwrapping | `user?.let { login(it) }` |
| Extension | Custom utility | `fun Context.showToast()` |
| `launch` | Background work | `viewModelScope.launch { }` |
| Overload | Same name, different params | `fun search(q: String)` / `fun search(q: String, n: Int)` |
| Override | Subclass replaces parent | `override val dataList: List<AthkarItem>` |

> *“Code is read much more often than it is written.”* — Keep it clean, keep it Kotlin! 🚀
