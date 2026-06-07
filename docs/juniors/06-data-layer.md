# Data Layer

## Overview

Athkarix has three data sources, all local (no network):

1. **Kotlin text constants** — all athkar content (10 files)
2. **JSON asset** — the 99 Names of Allah
3. **SharedPreferences** — notification settings

---

## 1. AthkarItem — The Universal Data Model

**File**: `data/model/AthkarItem.kt`

```kotlin
data class AthkarItem(
    val duaText: String?,     // The athkar/dua text (nullable)
    val footer: String? = null, // Source citation (optional)
)
```

Every athkar in the app is an `AthkarItem`. Some have just a text, others have a text + citation footer:

```kotlin
AthkarItem(duaText = "سبحان الله وبحمده")                          // just the text
AthkarItem(duaText = "اللهم...", footer = "متفق عليه")              // text + source
```

---

## 2. AthkarRepository — The Central Data Hub

**File**: `data/repository/AthkarRepository.kt`

An `object` singleton that holds all athkar data as pre-built lists:

```kotlin
object AthkarRepository {
    val athkarSabahList: List<AthkarItem> = listOf(
        AthkarItem(duaText = AthkarSabahText.TEXT_1, footer = AthkarSabahText.FOOTER_1),
        AthkarItem(duaText = AthkarSabahText.TEXT_2, footer = AthkarSabahText.FOOTER_2),
        // ... 22 more items
    )

    val athkarMassaList: List<AthkarItem> = listOf(...)
    val athkarAfterSalatList: List<AthkarItem> = listOf(...)
    val athkarBeforeGoToBedList: List<AthkarItem> = listOf(...)
    val tasbihList: List<AthkarItem> = listOf(...)
    val estigfarList: List<AthkarItem> = listOf(...)
    val hamdList: List<AthkarItem> = listOf(...)
    val salatAlaRasoulList: List<AthkarItem> = listOf(...)
    val duaMenQuranList: List<AthkarItem> = listOf(...)
    val duaMenSunnahList: List<AthkarItem> = listOf(...)
}
```

Each list is built from the corresponding `*Text.kt` constants. For example, `athkarSabahList` references `AthkarSabahText.TEXT_1` through `TEXT_24` and their footers.

ViewModels access it directly:

```kotlin
class AthkarSabahViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> get() = AthkarRepository.athkarSabahList
}
```

Note: The `get()` syntax means `dataList` is computed each time it's accessed — you could swap the repository data at runtime without recreating the ViewModel.

---

## 3. Text Constants — The Arabic Content

**Files**: `data/text/AthkarSabahText.kt`, `AthkarMassaText.kt`, etc. (10 files)

These files are **auto-generated from the Flutter Dart source** (marked with a comment at the top). Each is a Kotlin `object` containing `String` constants:

```kotlin
// Auto-generated from Flutter Dart source. Do not edit manually.
object AthkarSabahText {
    const val TEXT_1 = "اللَّهُمَّ بِكَ أَصْبَحْنَا..."
    const val FOOTER_1 = "متفق عليه"
    const val TEXT_2 = "الْحَمْدُ لِلَّهِ..."
    const val FOOTER_2 = "رواه مسلم"
    // ... etc for all 24 morning athkar
}
```

Why `object`? So any code can reference `AthkarSabahText.TEXT_1` without creating an instance. Why separate files? Each category has 9-57 items with Arabic text that can be hundreds of lines long.

The naming convention is simple: `TEXT_N` for the main Arabic, `FOOTER_N` for the source citation. They're always paired (even if the footer is empty).

---

## 4. JSON — The 99 Names

**File**: `assets/json/assma-hussna.json` (loaded at runtime)

The 99 Names of Allah are stored as JSON because they have a structured format (id, name, text) that would be tedious as constants:

```json
[
    {"id": 1, "name": "الرحمن", "text": "الرَّحْمَـٰنِ الرَّحِيمِ"},
    {"id": 2, "name": "الرحيم", "text": "الرَّحْمَـٰنِ الرَّحِيمِ"},
    ...
]
```

### AssmaHussnaItem — Model for JSON

```kotlin
data class AssmaHussnaItem(
    val id: Int,
    val name: String,
    val text: String,
) {
    companion object {
        fun fromJson(json: JSONObject): AssmaHussnaItem = AssmaHussnaItem(
            id = json.getInt("id"),
            name = json.getString("name"),
            text = json.getString("text"),
        )
    }
}
```

### AssmaHussnaService — JSON Loader

```kotlin
object AssmaHussnaService {
    private var cache: List<AssmaHussnaItem>? = null  // in-memory cache

    fun getAllAssmaHussna(context: Context): List<AssmaHussnaItem> {
        if (cache != null) return cache!!  // return cached data

        val json = context.assets.open("json/assma-hussna.json")
            .bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        cache = (0 until array.length()).map { i ->
            AssmaHussnaItem.fromJson(array.getJSONObject(i))
        }
        return cache!!
    }
}
```

Pattern:
1. Check cache first (avoids re-reading the file)
2. Open the file from `assets/` using `Context.assets.open()`
3. Parse the `JSONArray` into `AssmaHussnaItem` objects
4. Cache and return

The service also provides: `getById()`, `searchByName()`, `searchByText()`, `getCount()`, `clearCache()`, and `validateData()`.

---

## 5. SharedPrefsManager — Simple Persistence

**File**: `data/local/SharedPrefsManager.kt`

Wraps Android's `SharedPreferences` with clean Kotlin properties:

```kotlin
class SharedPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("athkarix_prefs", Context.MODE_PRIVATE)

    var morningEnabled: Boolean
        get() = prefs.getBoolean(KEY_MORNING_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_MORNING_ENABLED, value).apply()

    var morningHour: Int
        get() = prefs.getInt(KEY_MORNING_HOUR, 8)  // default: 8 AM
        set(value) = prefs.edit().putInt(KEY_MORNING_HOUR, value).apply()
    // ... same pattern for eveningEnabled, eveningHour, eveningMinute, morningMinute
}
```

The Kotlin property `var morningEnabled` looks like a regular variable:

```kotlin
prefsManager.morningEnabled = true     // calls set() → writes to SharedPreferences
val enabled = prefsManager.morningEnabled  // calls get() → reads from SharedPreferences
```

But behind the scenes, it's reading/writing to `SharedPreferences` using the `get()` / `set(value)` custom accessors.

---

## 6. NotificationService — Alarms and Notifications

**File**: `data/service/NotificationService.kt`

This is the most complex service. It:

1. Creates a notification channel (`athkar_reminders`) on Android 8+
2. Schedules daily alarms with `AlarmManager.setRepeating()`
3. Shows notifications when alarms fire

Contains a nested `BroadcastReceiver`:

```kotlin
class AthkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Build and show a notification based on the action
        // "SHOW_MORNING_REMINDER" or "SHOW_EVENING_REMINDER"
    }
}
```

Registered in `AndroidManifest.xml`:

```xml
<receiver android:name=".data.service.AthkarReminderReceiver" />
```

---

## Data Flow Summary

```
                    ┌─────────────────────────┐
                    │    AthkarRepository      │
                    │   (object singleton)     │
                    └──────┬──────────┬────────┘
                           │          │
              ┌────────────┘          └────────────┐
              ▼                                     ▼
┌───────────────────────┐          ┌──────────────────────────┐
│  *Text.kt constants   │          │  AssmaHussnaService      │
│  (10 files, object)   │          │  (JSON from assets/)     │
│  Compile-time data    │          │  Runtime data + cache    │
└───────────────────────┘          └──────────────────────────┘
         │                                   │
         ▼                                   ▼
┌───────────────────────────────────────────────────────────────┐
│                     ViewModels                               │
│  BaseAthkarViewModel subclasses read from Repository directly │
│  AssmaHussnaViewModel reads from AssmaHussnaService          │
│  NotificationSettingsViewModel reads from SharedPrefsManager │
└───────────────────────────────────────────────────────────────┘
```

For the layered unidirectional picture (UI → ViewModel → Repository → Assets) with the Mermaid graph, see **[`03-architecture-overview.md`](./03-architecture-overview.md)** § *The Big Picture: Data Flow*. The diagram above is the data-only zoom-in.

Key insight: There's no database, no API calls, no Retrofit, no Room. All data is either:
- Compiled into the app as Kotlin constants (athkar text)
- Loaded from a JSON asset (99 Names)
- Stored in SharedPreferences (settings)
