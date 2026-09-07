# Live Demo Script — Athkarix Android

> Slide 29 ("Live Demo") references this file. Every `file:line` reference below is verified against the source as of 2026-09-06.
> Axis 7 on the presentation map (Slide 2): **10 minutes** + compressed fallback plan **4 minutes**.

---

## 1. Environment Prerequisites (set up before the lecture)

| Requirement | Value | Evidence / Note |
|---|---|---|
| Emulator or physical device | API 34 (targeted and built for) | `app/build.gradle.kts:9,14` |
| Minimum runtime | API 24 `minSdk` | `app/build.gradle.kts:13` |
| Notification permission on API 33+ | **Granted manually** — the app declares `POST_NOTIFICATIONS` without a runtime request | `AndroidManifest.xml:4` |
| JDK | 17 (`VERSION_17` + `jvmTarget "17"`) | `app/build.gradle.kts:43-50` |
| Kotlin | 2.2.10 | `build.gradle.kts:3` |
| App on device | Pre-built debug APK (Section 2) | `applicationId = com.athkarix.app` |

**Before the lecture (two minutes):**

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm grant com.athkarix.app android.permission.POST_NOTIFICATIONS   # API 33+ devices
adb shell am start -n com.athkarix.app/.MainActivity
```

Performance note: Open the app and dismiss it from recents before going on stage to ensure a clean first frame.

---

## 2. Preflight Rehearsal

| Step | Command | Purpose |
|---|---|---|
| Build | `./gradlew assembleDebug` | Produces `app/build/outputs/apk/debug/app-debug.apk` |
| Install | `adb install -r app/build/outputs/apk/debug/app-debug.apk` | `-r` updates without deleting data |
| Permission (API 33+) | `adb shell pm grant com.athkarix.app android.permission.POST_NOTIFICATIONS` | Allows notifications to actually appear |
| WorkManager scheduling evidence | `adb shell dumpsys jobscheduler com.athkarix.app` | Two periodic jobs visible named `androidx.work.impl.background.systemjob.SystemJobService` after enabling notifications (Section 7) |

> All commands require `adb` on the PATH and a running emulator or connected device.

---

## 3. Scenarios (10 minutes)

Time allocation: (1) Home & theme 1:00 — (2) Sabah (Morning Athkar) AUTO_ADVANCE 2:30 — (3) Tasbih (Counter) INFINITE 1:30 — (4) Search with/without tashkeel 1:30 — (5) Assma Hussna exclusion + deep navigation 1:00 — (6) Notifications 2:00 — (7) Font toggle 0:30

---

### Scenario 1 — Home: Golden Theme & Category Grid (1:00)

**What to say:**
> "The entry point is a single screen: `NavHost` routes every path to its own screen, starting from `home` (AthkarixNavGraph.kt:64-67, :70-76). The screen shows the eleven content categories as a scrollable card list — not a grid of 8, there is no bottom navigation; navigation uses a drawer and a search button in the top bar. The theme is a consistent golden dark: `primaryGold = FFE082` on a dark background. Each card emits a single event via `SharedFlow` to the NavHost which handles navigation — the UI does not navigate itself."

**What to show:**
- Home screen: category cards, top search button (right icon), hamburger drawer.
- Open the drawer: notification settings, contact (WhatsApp/email), share app.
- Return to home via back gesture.

**Internal evidence:**
- 11-card list — `ui/screens/home/HomeScreen.kt:90-105`
- Side drawer — `ui/screens/home/HomeScreen.kt:117-143`
- Top bar with `primaryGold` colors — `ui/screens/home/HomeScreen.kt:155-159`
- Search button — `ui/screens/home/HomeScreen.kt:167-169`
- Single navigation event per card via SharedFlow — `ui/screens/home/HomeViewModel.kt:18-19,37-41`
- `home` as starting route — `navigation/AthkarixNavGraph.kt:66`

---

### Scenario 2 — Sabah (Morning Athkar): AUTO_ADVANCE Until Completion (2:30)

**What to say:**
> "The counter model has two modes: `AUTO_ADVANCE` (morning, evening, after-prayer, before-sleep, Quran dua, Sunnah dua) and `INFINITE_COUNT` (tasbih, istigfar, hamd, salat ala rasoul). In Sabah: each item has a `maxCount`; tapping the text increments the counter, and upon reaching `maxCount` it resets and auto-advances to the next page with haptic feedback (BaseAthkarViewModel.kt:83-91). When the end of the entire list is reached, a `ShowCompletion` event is emitted and the screen shows a Snackbar 'You have finished reading the morning athkar' (BaseAthkarViewModel.kt:92-95, completion message at AthkarSabahViewModel.kt:11). Note: the notification is a Snackbar, not a Toast. The content itself comes from a Kotlin object generated from a Flutter source — `data/text/AthkarSabahText.kt`."

**What to show (fast path to completion):**
1. Open "Sabah" (أذكار الصباح) from home.
2. Tap the page text: floating counter increments; then tap repeatedly until automatic page transition (you will see vibration and the number reset to zero).
3. Use the bottom slider (AthkarTextSlider.kt:162-177) or index drawer (menu button top-left) to jump to the start of the last section.
4. Tap until `maxCount` of the last section is reached (10 taps) → Snackbar "You have finished reading the morning athkar" appears.
5. "Reset" button (refresh icon) resets the page and counter.

**Internal evidence:**
- Counter modes — `viewmodel/BaseAthkarViewModel.kt:21,29`
- Tap increments counter (tap on text) — `ui/components/dua/AthkarTextSlider.kt:95`
- AUTO_ADVANCE branch (reset & advance) — `viewmodel/BaseAthkarViewModel.kt:85-96`
- Completion event emission — `viewmodel/BaseAthkarViewModel.kt:92-95`
- Snackbar display on event — `ui/screens/athkar/AthkarScreen.kt:71-80` (SnackbarHost at :109)
- Vibration on transition — `ui/screens/athkar/AthkarScreen.kt:83-87`
- Morning list (24 items, last one `maxCount=10`) — `data/repository/AthkarRepository.kt:8-33`
- Completion message — `viewmodel/AthkarSabahViewModel.kt:11`
- Index drawer & `goToPage` — `ui/screens/athkar/AthkarScreen.kt:93-102`

> For accuracy: there is no "completion button" and no 75% threshold — completion is automatic on the last page.

---

### Scenario 3 — Tasbih Counter: INFINITE_COUNT & Vibration (1:30)

**What to say:**
> "The tasbih screen runs in `INFINITE_COUNT` mode: cumulative counting with no page transitions (TasbihViewModel.kt:11). Each tap represents one dhikr; the counter goes as far as the user wants. Vibration is an achievement marker: long vibration every 100 taps (BaseAthkarViewModel.kt:77-79). The golden floating button is an independent counter that appears on 8 of 11 athkar screens, storing a count per `screenKey` in StateFlow — it lives in memory only, and is not persisted."

**What to show:**
1. Open "Tasbih" (التسبيح) from home.
2. Tap the text several times: the number increments cumulatively.
3. Tap the floating button: counts separately from the page counter.
4. Make sure to cross multiples of 100 to demonstrate the long vibration (you can now show the logic without waiting).

**Internal evidence:**
- `counterMode = INFINITE_COUNT` in tasbih — `viewmodel/TasbihViewModel.kt:11`
- Cumulative counter + vibration every 100 — `viewmodel/BaseAthkarViewModel.kt:75-81`
- Floating counter with `Map<screenKey, Int>` state and increment/reset — `viewmodel/FloatingCounterViewModel.kt:10-11,13-23`
- `performHapticFeedback` implementation — `ui/screens/athkar/AthkarScreen.kt:83-87`
- Floating button and reset button composition — `ui/screens/athkar/AthkarScreen.kt:145-165`

> For accuracy: there is no "target pattern 33" in the code. The fixed target is displayed in AUTO_ADVANCE (one of the morning items with `maxCount=7`, e.g. `AthkarRepository.kt:18`).

---

### Scenario 4 — Search: With and Without Tashkeel (1:30)

**What to say:**
> "Search is live and diacritics-tolerant: `DiacriticUtil` strips harakat and tanwin (Unicode marks U+064B..U+065F and U+0670) before `contains` matching, with lowercasing. This is why searching 'الله' matches text containing the fully-diacritized 'اللّٰه'. Query and item text normalization happens in `search()` (SearchViewModel.kt:48-72)."

**What to show:**
1. From home: search button top-right.
2. Type "الله": results returned from multiple categories with the category name under each result.
3. Type "اللّٰه" with tashkeel or "الله" (without tashkeel): same results — no difference.
4. Clear the text: returns the empty list (no-match state).

**Internal evidence:**
- `DiacriticUtil.remove` and regex — `util/DiacriticUtil.kt:5-7`
- Query normalization — `ui/screens/search/SearchViewModel.kt:54`
- `contains` matching — `ui/screens/search/SearchViewModel.kt:58-59`
- Live search field — `ui/screens/search/SearchScreen.kt:45-48`

---

### Scenario 5 — Search Excludes Assma Hussna + Deep Navigation (1:00)

**What to say:**
> "Search is built from only 10 categories — it explicitly excludes the 99 Names of Allah (SearchViewModel.kt:34-45). The result carries `categoryKey` and `index`, so the app navigates deeply to the item detail screen via a route carrying two written parameters: `search_result/{categoryIndex}/{itemIndex}`, then resolves `AthkarRepository.getItemByKey` (AthkarixNavGraph.kt:50,263-266,270-281)."

**What to show:**
1. Tap a result from Sabah → full text detail screen with share button and font tools.
2. Now search for one of the 99 Names (e.g. "الرحمن"): nothing — because it is outside the scope of the 10 categories (explicitly stated on Slide 27).

**Internal evidence:**
- `assma_hussna` exclusion — `ui/screens/search/SearchViewModel.kt:34-45`
- Result route construction — `navigation/AthkarixNavGraph.kt:263-266`
- Written arguments and item resolution — `navigation/AthkarixNavGraph.kt:270-281`
- Route constant `SEARCH_RESULT` — `navigation/AthkarixNavGraph.kt:50`

---

### Scenario 6 — Notifications: Enable + Change Time + WorkManager Evidence via adb (2:00)

**What to say:**
> "Settings are read once in the ViewModel from SharedPreferences and exposed as StateFlow — guaranteeing a single initial frame with no flicker (NotificationSettingsViewModel.kt:16-32). The toggle saves then schedules or cancels (:35-53); and changing the time re-schedules if currently enabled (:56-76). Scheduling uses `PeriodicWorkRequestBuilder` for 1 day with UPDATE policy: at the requested hour `ReminderWorker` fires, picks a random dhikr from the morning or evening list, and displays a notification (NotificationService.kt:40-49, ReminderWorker.kt:17-45). The periodic work is managed by WorkManager and survives device reboots."

**What to show:**
1. From the drawer: "Notification Settings" (إعدادات الإشعارات).
2. Enable morning: current time appears (default 08:00) and enable evening (17:00).
3. Change morning time via the Time Picker dialog (long-press on the time).
4. From the terminal:
   ```bash
   adb shell dumpsys jobscheduler com.athkarix.app
   ```
   Observe two periodic jobs named `androidx.work.impl.background.systemjob.SystemJobService`.
5. Disable "evening" (if desired) and observe the job disappearing after a moment.

**Internal evidence:**
- Enable/cancel with save — `viewmodel/NotificationSettingsViewModel.kt:35-53`
- Re-schedule on time change — `viewmodel/NotificationSettingsViewModel.kt:56-76`
- Toggle and Time Picker UI — `ui/screens/settings/NotificationSettingsScreen.kt:55-77`
- Periodic scheduling (1 DAY + UPDATE) — `data/service/NotificationService.kt:40-49`
- Delay calculation to requested hour — `data/service/NotificationService.kt:59-71`
- `ReminderWorker` and random selection with BigText — `data/service/ReminderWorker.kt:17-45`
- Permission declared without runtime request — `AndroidManifest.xml:4`

---

### Scenario 7 — Font Toggle (0:30)

**What to say:**
> "The two font buttons toggle between Amiri and Noto Naskh Arabic via `FontViewModel.toggleFont` (FontViewModel.kt:23-25), and size changes in ±2 steps within [24, 42] (:16-17,27-39). Honest reminder: both family and size live in memory only."

**What to show:**
1. Open any athkar screen (e.g. Sabah) and tap the text icon (top bar).
2. Toggle the font twice: the display swaps instantly between the two fonts.
3. Increase then decrease the size for a quick demo.

**Internal evidence:**
- `toggleFont` and `setFont` — `viewmodel/FontViewModel.kt:19-25`
- Font size adjustment (default 32, range 24–42) — `viewmodel/FontViewModel.kt:10-17,27-39`
- Font name to file mapping — `ui/components/dua/AthkarTextSlider.kt:60-67`
- Font button in the top bar — `ui/screens/athkar/AthkarScreen.kt:120`

---

## 4. Compressed Fallback Plan (4 minutes)

When time is short or device malfunctions: Home (0:45) ← Sabah AUTO_ADVANCE (1:30) ← Tasbih INFINITE (1:00) ← Search with tashkeel (0:45).

| Scenario | At | What to show |
|---|---|---|
| 1 | 0:45 | Home + 11 cards + golden theme |
| 2 | 2:15 | Sabah: slider jump to last page then completion + Snackbar |
| 3 | 3:15 | Tasbih: cumulative counter + vibration at 100 |
| 4 | 4:00 | Search "الله" without tashkeel — instant match |

**Screenshot contingency:** Keep ready-made screenshots for the evening screen, notification settings, and search result detail (include in `presentation/` or show as embedded images) to cover any scenario not shown live.

---

## 5. "Say" and "Don't Say" Lists

### Say (positive, evidence-backed phrasing)
- "**Offline-first** — no network at all, no network dependencies in `app/build.gradle.kts`"
- "**Manual DI** — single `AppModule` object makes the dependency graph visible and traceable (AppModule.kt:22-24)"
- "Preferences are persisted via SharedPreferences (6 keys) and WorkManager scheduling survives reboots"
- "**Repository is the single source of truth** — `AthkarRepository`"
- "First frame is deterministic: the ViewModel reads preferences once and exposes StateFlow"
- "52 unit tests in 8 files" — exact count, not a generalization
- "Content is generated from Flutter Dart and carries declared anomalies such as duplicate TEXT_26" — honesty

### Don't say
- ✗ "No data persistence in the app" / "No persistence" — negative phrasing invites follow-up questions; say it correctly: preferences are persisted, counters and font are in-memory (declared behavior).
- ✗ "We tested everything" — coverage is 52 tests across 6 ViewModels + utilities; no androidTest, no Compose UI tests, no notification service tests.
- ✗ Do not claim search includes the 99 Names of Allah — it explicitly excludes them.
- ✗ Do not claim the app requests notification permission at runtime — it is declared only, and must be manually granted on API 33+.
- ✗ Do not say "completion button" or "vibration at 75%" — the actual mechanism is AUTO_ADVANCE and vibration on transition/completion (every 100 in tasbih).

---

## 6. Quick Pre-Stage Checklist

- [ ] `./gradlew assembleDebug` finishes without errors
- [ ] `adb install -r` succeeds on device/emulator
- [ ] Notification permission granted (API 33+)
- [ ] Fonts (Amiri + Noto Naskh) appear in quick test
- [ ] Know the location of the Sabah slider/index to jump to the last section
- [ ] Contingency screenshots ready (for evening, settings, search result detail)
