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

### 10. `StateFlow` vs `MutableStateFlow` + Backing Property

A one-paragraph pointer is enough at this stage — the **full deep dive** (private-mutable / public-readonly split, `collectAsState()`, why-`StateFlow`-not-`var`, Flutter GetX `.obs` comparison table) lives in **[`04-viewmodel-deep-dive.md`](./04-viewmodel-deep-dive.md)** under *Deep Dive: MutableStateFlow vs StateFlow vs asStateFlow()*. Read it once you reach the ViewModel chapter.

The mental model in one sentence: **`StateFlow` is a radio station — the UI subscribes once and rebuilds whenever the value changes.**

> [!TIP]
> **Coming from Flutter (GetX)?** `MutableStateFlow` is the equivalent of `.obs`; `collectAsState()` is the equivalent of `Obx(() => …)`. The full side-by-side table is in `04`.

#### 🎯 Backing Property Pattern (نمط الملكية الخلفية)

**English:**
This is the **#1 most important pattern** you will see in every ViewModel in Athkarix. Understanding it unlocks all the reactive state management in the app.

**بالعربية:**
هذا هو **أهم نمط** ستشاهده في كل ViewModel في تطبيق Athkarix. فهمه يفتح لك الباب لفهم إدارة الحالة التفاعلية في التطبيق بأكمله.

```kotlin
class FontViewModel : ViewModel() {

    private val _fontSize = MutableStateFlow(28.6f)   // 1
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()  // 2
}
```

##### 🤝 Bridge from Java: Private Field + Getter/Setter

**English:**
If you come from Java, this pattern will feel familiar. It is the exact same idea as a **private field with a public getter** — but adapted for reactive streams:

```java
// Java — traditional encapsulation
public class FontViewModel {
    private float fontSize = 28.6f;          // private field ← like _fontSize

    public float getFontSize() {              // public getter  ← like fontSize (read-only)
        return fontSize;
    }

    public void setFontSize(float value) {    // public setter  ← like increaseFontSize() / decreaseFontSize()
        this.fontSize = value;
    }
}
```

| Java Concept | Kotlin Equivalent |
|-------------|-------------------|
| `private float fontSize` | `private val _fontSize = MutableStateFlow(28.6f)` |
| `public float getFontSize()` | `val fontSize: StateFlow<Float> = _fontSize.asStateFlow()` |
| `public void setFontSize(float)` | `fun increaseFontSize()` / `fun decreaseFontSize()` |
| Encapsulation (hide data, expose behavior) | Same principle — `private` mutable, `public` read-only |

The key difference: in Java, the getter returns a plain value. In Kotlin + Flows, `fontSize` is a **reactive stream** — the UI subscribes to it and gets **automatically notified** whenever the value changes.

**بالعربية:**
إذا كنت قادماً من Java، ستجد هذا النمط مألوفاً. إنه نفس فكرة **الحقل الخاص مع دالة إحضار عامة** — لكن مُكيّف للتدفقات التفاعلية:

| مفهوم Java | المقابل في Kotlin |
|------------|-------------------|
| `private float fontSize` | `private val _fontSize = MutableStateFlow(28.6f)` |
| `public float getFontSize()` | `val fontSize: StateFlow<Float> = _fontSize.asStateFlow()` |
| `public void setFontSize(float)` | `fun increaseFontSize()` / `fun decreaseFontSize()` |
| التغليف (إخفاء البيانات، إظهار السلوك) | نفس المبدأ — قابل للتعديل `private`، للقراءة فقط `public` |

الفرق الجوهري: في Java، الدالة `getFontSize()` ترجع قيمة عادية. في Kotlin مع Flows، المتغير `fontSize` هو **تدفق تفاعلي** — الـ UI يشترك فيه ويتم **إعلامه تلقائياً** كلما تغيرت القيمة.

##### Line-by-Line Breakdown (English)

| # | Variable | Type | Visibility | Purpose |
|---|----------|------|------------|---------|
| 1 | `_fontSize` | `MutableStateFlow<Float>` | `private` | Holds the mutable value. ViewModel writes to this. |
| 2 | `fontSize` | `StateFlow<Float>` | `public` | Exposes the same value as **read-only** to the UI. |
| `.asStateFlow()` | *(conversion)* | — | — | Bridges the two: removes the `set` capability. |

##### Line-by-Line Breakdown (بالعربية)

| # | المتغير | النوع | الظهور | الهدف |
|---|---------|-------|--------|-------|
| 1 | `_fontSize` | `MutableStateFlow<Float>` | `private` | يخزن القيمة القابلة للتعديل. الـ ViewModel يكتب فيه. |
| 2 | `fontSize` | `StateFlow<Float>` | `public` | يعرض نفس القيمة ولكن **للقراءة فقط** للـ UI. |
| `.asStateFlow()` | *(تحويل)* | — | — | يربط بينهما: يزيل خاصية التعديل (`set`). |

##### Why this pattern?

**English — Encapsulation:**
By keeping the mutable version `private`, you guarantee that **only the ViewModel** controls state changes. The UI (Composable functions) cannot accidentally corrupt the state.

```kotlin
// ✅ Correct — ViewModel controls state
viewModel.increaseFontSize()  // ViewModel changes _fontSize internally

// ❌ Impossible — fontSize is read-only
// viewModel.fontSize.value = 50f  // Compile error!
```

**بالعربية — التغليف:**
من خلال جعل النسخة القابلة للتعديل `private`، أنت تضمن أن **الـ ViewModel فقط** هو من يتحكم في تغييرات الحالة. الـ UI لا يستطيع إفساد الحالة عن طريق الخطأ.

##### Visualization

```
┌─────────────────────────────────┐
│          FontViewModel          │
│                                 │
│  private _fontSize  ──asStateFlow──►  public fontSize  ──collectAsState──►  UI
│  (MutableStateFlow) │           │  (StateFlow)         │                 │
│                     │ can write │                      │ can read         │
│  changeFont() ──────┘           │                      ◄─────────────────┘
│  increaseFontSize()             │
│  decreaseFontSize()             │
└─────────────────────────────────┘
```

**English:**
The arrow shows the **one-way data flow**: ViewModel writes → UI reads. Never the reverse.

**بالعربية:**
السهم يوضح **تدفق البيانات باتجاه واحد**: الـ ViewModel يكتب ← الـ UI يقرأ. أبداً العكس.

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

> [!TIP]
> **From Java?** A lambda `{ viewModel.goToNextPage() }` is like a `Runnable` — an object that wraps the code for later execution.

### 12. Function Call vs Function Reference (`()` vs `::`)

**The #1 bug juniors make in Compose:** calling a function instead of passing it.

**English — The Difference:**

```kotlin
// ❌ WRONG — calls the function IMMEDIATELY
HomeButtonItem("الإستغفار", "estigfar", viewModel.goToEstigfar())
//                                    👆 The () executes goToEstigfar() right now,
//                                    before the user even sees the button!

// ✅ CORRECT — passes the function to be called LATER
HomeButtonItem("الإستغفار", "estigfar", viewModel::goToEstigfar)
//                                    👆 The :: passes a *reference* to the function.
//                                    The button stores it and calls it onClick only.
```

| Syntax | Meaning | When does it run? |
|--------|---------|-------------------|
| `viewModel.goToEstigfar()` | **Call** the function | **Immediately**, at this exact line of code |
| `viewModel::goToEstigfar` | **Reference** the function | **Later**, when the button is clicked |
| `{ viewModel.goToEstigfar() }` | **Lambda wrapping** the call | **Later**, when the button is clicked |

**بالعربية — الفرق:**

إذا كتبت `viewModel.goToEstigfar()` (باستخدام الأقواس): أنت تخبر التطبيق: "قم بتنفيذ الدالة الآن فوراً بمجرد بناء زر الـ HomeButtonItem"، وهذا سيؤدي إلى الانتقال لصفحة الاستغفار مباشرة **قبل أن يضغط المستخدم على الزر**، وهو سلوك خاطئ.

أما باستخدام `viewModel::goToEstigfar`: أنت تخبر الزر: "خذ هذه الدالة واحتفظ بها لديك، وعندما يقوم المستخدم بالضغط فعلياً على الزر، قم بتنفيذها". أنت تمرر الدالة كـ **كائن (Object)** أو كـ "مؤشر" لها.

```kotlin
// ✅ الطريقة المطولة (Lambda)
HomeButtonItem("الإستغفار", "estigfar", { viewModel.goToEstigfar() })

// ✅ الطريقة المختصرة الذكية (Function Reference) — الأفضل
HomeButtonItem("الإستغفار", "estigfar", viewModel::goToEstigfar)
```

> [!TIP]
> **Remember:** `()` means **now**. `::` means **later**. In Compose, almost everything should be **later**.

### 13. Scope Functions (`let`, `apply`)
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

### 14. Extension Functions
Want to add a new method to a class without modifying its source code? You can *extend* any existing class — even ones you don't own.

```kotlin
// Real example from AthkarixNavGraph.kt (line 66)
// `composable` is an extension function on NavGraphBuilder, defined by the AndroidX library:
fun NavGraphBuilder.composable(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
)

// That's what lets us call it inside NavHost { } like a built-in method:
NavHost(navController = navController, startDestination = Routes.HOME) {
    composable(Routes.HOME) {   // ← `composable` is an extension function!
        val vm = remember { AppModule.provideHomeViewModel() }
        HomeScreen(viewModel = vm, onNavigate = { route -> navController.navigate(route) })
    }
}
```

> [!TIP]
> The pattern is `fun ReceiverType.functionName(...)`. The receiver (`NavGraphBuilder` here) becomes `this` inside the function body — exactly like `String` is the receiver in `fun String.toArabicNumerals()`.

### 15. Compose Effects (`remember`, `LaunchedEffect`)
For the full story on how Compose remembers state and runs side effects (including how `remember { AppModule.provideHomeViewModel() }` ties into the manual DI graph), see **[`05-ui-layer.md`](./05-ui-layer.md)** § *Key Components* and **[`07-navigation-and-di.md`](./07-navigation-and-di.md)** § *How NavGraph Uses AppModule*.

The mental model in one sentence each:

* **`remember`** — "Keep this value across recompositions of the same screen."
* **`LaunchedEffect(key)`** — "Run this coroutine once, and re-run it if `key` changes."

---

## 📊 Quick Cheatsheet
