# Project Audit — Athkarix Android

Full codebase analysis covering pros, bugs, performance, over-engineering, architecture, and code quality.

---

## Pros

- **Clean MVVM architecture** — clear separation of concerns, ViewModels own state via StateFlow
- **Excellent abstract base pattern** — `BaseAthkarViewModel` eliminates duplicating page/counter logic across 11 categories
- **Manual DI keeps it simple** — no Hilt/Dagger/Koin annotation magic, easy to understand
- **Arabic-first RTL** — `reverseLayout = true`, `AutoMirrored` icons, proper RTL support
- **Single Activity** — modern best practice, no Activity explosion
- **Reactive state** — all state goes through `StateFlow` -> `collectAsState()`, UI recomposes automatically
- **Compile-safe navigation** — `Routes` object prevents route string typos
- **No network dependencies** — everything is local, no Retrofit/OkHttp, minimal attack surface
- **Compose-only UI** — no XML layout files for screens, consistent modern stack

---

## Bugs

### Critical

| # | Bug | File:Line | Why it matters |
|---|-----|-----------|----------------|
| 1 | **Alarm cancel never works** — `cancel()` creates an Intent *without* an action, but the scheduled alarm Intents have `action = "SHOW_MORNING_REMINDER"` / `"SHOW_EVENING_REMINDER"`. `PendingIntent` equality compares the action, so cancel finds no match. | `NotificationService.kt:82-87` | Users cannot turn off notifications once enabled |
| 2 | **`setRepeating()` is unreliable on Android 12+** — deprecated in API 31, inexact alarm limits may delay or skip alarms. `RTC_WAKEUP` may not wake the device reliably. | `NotificationService.kt:70-75` | Notifications may not fire on modern devices |
| 3 | **Notification ID is `(System.currentTimeMillis()).toInt()`** — `Long.toInt()` truncates to 32 bits, can produce negative IDs on some API levels (notification with negative ID may crash). Also duplicates possible within same millisecond. | `NotificationService.kt:30` | Potential crash or overwritten notifications |
| 4 | **Alarm scheduled in the past fires immediately** — `Calendar` is set to today's hour/minute. If that time has already passed, `setRepeating()` fires the alarm right away and continues on that schedule. | `NotificationService.kt:65-69` | Alarm fires immediately when user toggles notification on |

### Important

| # | Bug | File:Line | Why it matters |
|---|-----|-----------|----------------|
| 5 | **FAB `increment()` never resets at 100** — `FloatingCounterViewModel` has two methods: `increment()` (unbounded) and `incrementUntil100()` (0-99). The FAB calls `increment()`, so the counter grows indefinitely. | `FloatingCounterViewModel.kt:13-15`, `AthkarScreen.kt:93` | Tasbeeh counter never wraps, defeats the purpose |
| 6 | **Time picker is a no-op** — clicking the time button in settings runs `{ /* show TimePicker */ }`. User cannot actually change notification time. | `NotificationSettingsScreen.kt:76,96` | Notification settings screen is non-functional for time editing |
| 7 | **`ViewEvent.NavigateTo` and `NavigateBack` silently ignored in AthkarScreen** — the `when` block has `else -> {}`, so if `goToHome()` emits `NavigateTo("home")`, nothing happens. | `AthkarScreen.kt:52-56` | "Go to home" button does not work |

---

## Performance

| # | Issue | File | Impact |
|---|-------|------|--------|
| 1 | **`DiacriticUtil.remove()` recomputed every keystroke in search** — all 300+ items have diacritics stripped on every `onValueChange`. No memoization of normalized text. | `SearchViewModel.kt:51-56` | Minor on modern devices, but noticeable lag on low-end with rapid typing |
| 2 | **`AthkarRepository` initializes all 11 category lists eagerly** — every `AthkarItem` object is created when the `object` is first accessed, even if user never opens some categories | `AthkarRepository.kt` | ~300 objects, negligible (~50KB), but unnecessary |
| 3 | **3 JPEGs in drawable/ at presumably full quality** — `bg_home.jpg`, `bg_91k.jpg`, `athkari5.jpg`. Two are unused, the one used has no downsample config | `res/drawable/` | Memory - background image at screen resolution with no compression |

---

## Over-engineering / Overkill

| # | Issue | Why |
|---|-------|-----|
| 1 | **10 identical `maxPageCounters`** — 10 of 11 ViewModel subclasses define `maxPageCounters = List(N) { 1 }`. Only `AthkarSabahViewModel` differs. The hierarchy could be a single `AthkarViewModel` taking a `maxPageCounters` parameter, eliminating 10 files. | 10 empty files for no gain |
| 2 | **`HomeViewModel` with 12 identical `goTo*()` methods** — each is `fun goToX() = navigate("x")`. The routes live in button data already; the ViewModel layer adds no value here. | Extra indirection without benefit |
| 3 | **`AssmaHussnaService` implements 10 methods, only 2 are used** — `getById()`, `searchByName()`, `searchByText()`, `getCount()`, `clearCache()`, `validateData()` are never called from anywhere. | YAGNI |
| 4 | **`CustomDrawer` defined but never used** — `HomeScreen` inlines drawer content instead. Dead component. | `ui/components/CustomDrawer.kt` is dead code |
| 5 | **`SearchResultScreen` defined but never used** — NavGraph uses `PlaceholderScreenWithVM` for the search result route. | `ui/screens/search/SearchResultScreen.kt` is dead code |
| 6 | **`FontScaleUtil` defined but never used** — the `scaledFontSize()` and `isTablet()` composables are never imported anywhere. | `util/FontScaleUtil.kt` is dead code |
| 7 | **`bg_91k.jpg` and `athkari5.jpg` unused** — only `bg_home.jpg` is referenced in code. Two images shipped in APK for no reason. | Wasted APK size |

---

## Architecture Issues

| # | Issue | Why it matters |
|---|-------|----------------|
| 1 | **ViewModels don't survive process death** — Created with `remember { AppModule.provide*() }` instead of `viewModel()` composable. If Android kills the app, all state is lost. | User loses their page position and counter |
| 2 | **No SavedStateHandle** — Even with proper `viewModel()` scoping, ViewModels don't persist state across process death | Same as above - state loss is guaranteed |
| 3 | **`AppModule` nullable backing fields can cause NPE** — Uses `?:` assignment + `!!`. If the `if` block were somehow skipped (e.g., race condition), the `!!` crashes. The pattern is thread-unsafe. | `AppModule.kt:24-47` — race condition on concurrent access (unlikely in practice but not provably safe) |
| 4 | **Zero tests** — no `src/test/` or `src/androidTest/` directories. Not a single unit test, integration test, or UI test. | Any regression goes undetected |
| 5 | **`AthkarTextSlider` font family uses hardcoded string comparison** — `fontFamily == "Amiri"` rather than an enum or sealed class. If the string changes, the font selection silently breaks. | `AthkarTextSlider.kt:79` — fragile |

---

## Code Quality Issues

| # | Issue | File:Line |
|---|-------|-----------|
| 1 | **`getShareText()` returns empty string when text is null** — shares an empty string with no user feedback | `BaseAthkarViewModel.kt:78` |
| 2 | **`WhatsAppUtil` swallows all exceptions silently** — if both WhatsApp and Play Store fail, user gets no feedback | `WhatsAppUtil.kt:17-24` |
| 3 | **`cancel()` method doesn't match intent signature of scheduled alarms** — cancel intent lacks `action` field | `NotificationService.kt:83` |
| 4 | **`String.format("%02d", ...)` in Compose screen** — should use padding in Compose or a utility function | `NotificationSettingsScreen.kt:78,98` |
| 5 | **Kotlin 2.2.10** — This version doesn't exist in stable Kotlin releases. Latest stable is 2.1.x. Build may be using an unstable or custom build. | `build.gradle.kts:3` |

---

## Summary

**Ready for production?** Not yet

**Critical issues:** Notifications can't be turned off (cancel bug), notifications may not fire on Android 12+ (deprecated API), notification ID can cause crashes (negative int), alarms fire immediately if time has passed

**Top 3 fixes:**
1. Fix `cancel()` in `NotificationService` to match intent action
2. Replace `AlarmManager.setRepeating()` with `WorkManager` for reliable scheduling
3. Fix `FloatingCounterFab` to use `incrementUntil100()` for proper tasbeeh wrap

**Dead code to remove:** `CustomDrawer.kt`, `SearchResultScreen.kt`, `FontScaleUtil.kt`, `bg_91k.jpg`, `athkari5.jpg`
