# Q&A Guide — Athkarix Android

> Every `file:line` reference below is verified against the source as of 2026-09-06.
> **30 questions** across 9 categories. Each question includes: a speakable answer (30–60s) + `<file>:<line>` reference + trade-off line + potential follow-up question.
> Note: what is stated here as declared behavior (counters/font in memory, 99 Names excluded from search, no runtime notification permission request) must be stated as such — not overstated.

---

## Category 1 — Architecture & System Design (Architecture)

### Q1: Why MVVM + StateFlow instead of another architecture like MVI?
**Answer (≈40s):** We adopted MVVM because the modern Android official path prefers it with Compose, and it gives us a clear separation: Compose screens read `StateFlow` only, the ViewModel holds business logic, and the Repository is the single source of truth. The backing `MutableStateFlow` is private and `asStateFlow()` is read-only, protecting state from external mutation — a strict interface contract (BaseAthkarViewModel.kt:31-39). We did not move to MVI because it adds an extra event/reducer layer that adds complexity without need for an app of this size; StateFlow suffices.

**Trade-off:** Centralized control but a slightly older sample pattern; if the app grows and complex branching state emerges we may revisit MVI.

**Follow-up:** How do you handle state diagrams, or do you use built-in Kotlin Flows like `combine` to merge sources?

---

### Q2: Every screen has its own ViewModel — how do you wire them all together?
**Answer (≈40s):** Navigation is handled through `AthkarixNavGraph`, which binds every route to its screen and invokes `AppModule.provideXxxViewModel()` (AthkarixNavGraph.kt:70-76). Dependency injection is manual in a single `AppModule` object: shared services (SharedPrefsManager, FontViewModel, FloatingCounterViewModel) are stored as singletons and reused across the process lifetime, while each per-category ViewModel is created `fresh` on every route entry (AppModule.kt:32-51,53-65). The ViewModel has no knowledge of any other screen; it communicates through `SharedFlow` events such as navigation (HomeViewModel.kt:18-19,37-41) which the NavHost captures.

**Trade-off:** Explicit singletons in memory vs. classes instantiated/destroyed with navigation — clear and easy to trace, but manual.

**Follow-up:** Why make `FontViewModel` a singleton while every athkar screen has its own ViewModel?

---

### Q3: What is the Repository's role as "single source of truth"?
**Answer (≈35s):** `AthkarRepository` is a single object holding all athkar lists as `List<AthkarItem>`, sourced from embedded Kotlin text objects (data/repository/AthkarRepository.kt:8-33 for morning, :86-122 for tasbih, :321-538 for 99 Names, and more). Every ViewModel fetches its `dataList` from it (AthkarSabahViewModel.kt:9), and search results are resolved via `getItemByKey` (AthkarRepository.kt:540-556). Data is entirely local — no network — achieving true offline-first (app/build.gradle.kts:53-67 has no network dependencies).

**Trade-off:** Compile-time reads — fast and no loading required, but content is frozen inside the code.

**Follow-up:** If you wanted to update content without updating the APK, how would you restructure the Repository?

---

### Q4: Why "manual DI" without Hilt/Dagger?
**Answer (≈35s):** We deliberately avoided Hilt and Dagger (explicit rule in AGENTS.md). `AppModule` is a simple object that makes the dependency graph visible and easy for a junior developer to trace (AppModule.kt:22-24, comment explains this intent). The app is small with a limited number of dependencies, so manual injection is more than sufficient without the annotation processing overhead and build-time/size bloat.

**Trade-off:** Simplicity and transparency at the cost of scalability; if the team grows and layers proliferate we may return to a library.

**Follow-up:** How do you test the internal ViewModel that depends on `AppModule`?

---

## Category 2 — Compose State / Recomposition

### Q5: What is "first render" vs "recomposition" in this design?
**Answer (≈45s):** "Initial composition" is the moment the Composable UI tree is built for the first time on a given route — for example, when entering `ATHKAR_SABAH`. "Recomposition" is the re-execution of that Composable later when its read state changes. In `AthkarScreen`, we read `currentPageIndex` and `currentPageCounter` via `collectAsState()` (AthkarScreen.kt:63-64); at initial composition the initial values are shown (page 0, counter 0). On every StateFlow update, only the parts that read it recompose. The key: reading via `collectAsState` inside the Composable triggers recomposition; `remember` without conditional reads does not.

**Trade-off:** This is equivalent to "hydration" on the web: state is pre-filled in the ViewModel so there is no flash or empty state at first render.

**Follow-up:** Where exactly does recomposition trigger in `AthkarTextSlider` when the page changes?

---

### Q6: What happens to ViewModel state on process death?
**Answer (≈45s):** When the process is recreated (rotation, low-memory kill) all ViewModels are destroyed — only the Activity survives rotation because it is recreated but the ViewModelStore is retained. On actual process death, ViewModels are recreated from scratch: every `MutableStateFlow` is reset to its initial value (e.g. `currentPageIndex = 0`, BaseAthkarViewModel.kt:32-36). We do not use `SavedStateHandle` (no usage anywhere in the source). So: user page and counters **are lost** on real process death. The only exception: notification settings are read from SharedPreferences on creation and thus restored (NotificationSettingsViewModel.kt:16-32).

**Trade-off:** Simplicity in process-death handling (no save/restore) vs. user experience that may reset after a system reboot.

**Follow-up:** If you wanted to save the current reading page, where would you place it — `SavedStateHandle` or SharedPreferences and why?

---

### Q7: What data is read at first composition vs what may change?
**Answer (≈45s):** Three categories. (1) **Fixed per route:** `dataList` is built once in the ViewModel from the Repository and does not change (AthkarSabahViewModel.kt:9). (2) **Read at first composition then may change:** page/counter via `collectAsState` (AthkarScreen.kt:63-64), font/size from `FontViewModel` (AthkarTextSlider.kt:57-59). (3) **Read from preferences once on creation:** ViewModel keys (NotificationSettingsViewModel.kt:16-32) — read then exposed as StateFlow, so the first frame is always deterministic with no flicker. This is unlike a web project where some data arrives from the server after first render — here everything is local so first render is in sync with state.

**Trade-off:** One-time reads provide synchronization, but if preferences are changed externally the ViewModel will not pick them up until it is recreated.

**Follow-up:** Are `selectedFont` and `fontSize` stored in SharedPreferences? (Answer: No — in memory only, FontViewModel.kt:10-14.)

---

### Q8: How do you avoid unnecessary recomposition here?
**Answer (≈30s):** We rely on focused reads via `collectAsState` and `derivedStateOf` derivatives to minimize change propagation. Example: computing `fontFamily` from the font name inside `remember { derivedStateOf { ... } }` (AthkarTextSlider.kt:60-67) so it is only recomputed when the name changes. For each page in the `HorizontalPager` we read `viewModel.dataList.getOrNull(page)` (AthkarTextSlider.kt:98) — no need for a per-item `remember` because the list is immutable. We do not use excessive state hoisting or non-deterministic keys.

**Trade-off:** Standard practices with no unnecessary `remember` calls — a balance between performance and readability.

**Follow-up:** Do you use `collectAsStateWithLifecycle` to stop collection when off-screen?

---

## Category 3 — Navigation & State Management

### Q9: How is navigation managed and routes are built?
**Answer (≈40s):** A `Routes` object holds route constants as a single source of truth (AthkarixNavGraph.kt:35-51) — no magic strings. `NavHost` starts at `home` (AthkarixNavGraph.kt:64-67) and maps every route to its screen. `HomeViewModel` emits navigation events through `SharedFlow` so the UI does not navigate itself (HomeViewModel.kt:37-41), and the NavHost captures them to call `navController.navigate` (AthkarixNavGraph.kt:70-76 with HomeScreen.kt:80-86).

**Trade-off:** Navigation separated from the UI makes it testable but adds an intermediate event layer.

**Follow-up:** How do you handle a result returned from a route?

---

### Q10: How does deep linking work for a search result?
**Answer (≈40s):** The `SEARCH_RESULT` route carries two written parameters: `search_result/{categoryIndex}/{itemIndex}` (AthkarixNavGraph.kt:50). When a result is tapped the route is built with variable substitution (AthkarixNavGraph.kt:263-266), and a `composable` declares two arguments `NavType.StringType` and `NavType.IntType` (AthkarixNavGraph.kt:270-275). The item is resolved via `remember(categoryIndex, itemIndex) { AthkarRepository.getItemByKey(...) }` (AthkarixNavGraph.kt:279-281) and its details are displayed.

**Trade-off:** Written parameters are type-safe but couple the display layer to data resolution at the route definition point.

**Follow-up:** What happens if `itemIndex` is out of bounds? (Answer: null, then an empty screen, AthkarixNavGraph.kt:298.)

---

### Q11: How do you keep shared singletons like font and floating counter alive across screens?
**Answer (≈35s):** In the NavGraph we create `fontVM` and `floatingCounterVM` once via `remember` at the top and pass them to every athkar screen (AthkarixNavGraph.kt:57-58). The same mechanism works in `AppModule` as singletons (AppModule.kt:39-51). This keeps the font, its size, and the "cross-screen" counter shared within a single session. But they are **in memory only** — not persisted to preferences — so they are lost on process death.

**Trade-off:** Live sharing is easy but lost on reboot — this is intentional, declared behavior.

**Follow-up:** Why not persist the user's preferred font size so it survives across sessions?

---

### Q12: How is the "exit" state managed from home?
**Answer (≈30s):** `HomeScreen` holds `showExitDialog` as `remember { mutableStateOf(false) }` (HomeScreen.kt:77), then `ExitGuard` displays a confirmation dialog; on confirm it calls `finishAffinity()` to end the activity (HomeScreen.kt:107-115). The state is local to the UI because it is short-lived and does not need to be lifted to the ViewModel.

**Trade-off:** Lifting local state to the ViewModel would be overcomplication; keeping it in Compose preserved simplicity.

**Follow-up:** What if the user presses Home (system button) instead of Back?

---

## Category 4 — Data Pipeline & Text Processing

### Q13: Where does the content come from and how is it modeled?
**Answer (≈35s):** Content is generated from Kotlin files (11 files in data/text/, e.g. AthkarSabahText.kt) and assembled into `AthkarItem` lists in `AthkarRepository` (AthkarRepository.kt:8-33 for morning). The `AthkarItem` model has three fields: `duaText: String?`, `footer: String?`, `maxCount: Int` (AthkarItem.kt:4-7). This content was ported from the original Flutter app — it is frozen in code, not downloadable or editable outside of a build.

**Trade-off:** Embedded content ships with the APK (fast/offline) but is hard to update without a new release.

**Follow-up:** Where was this content ported from, and how is its integrity verified?

---

### Q14: How does search matching work, and does it miss diacritics?
**Answer (≈40s):** Search relies on `DiacriticUtil.remove` which strips tashkeel marks U+064B..U+065F and U+0670 (DiacriticUtil.kt:5-7). In `SearchViewModel.search()`, the query and item texts are normalized together (diacritics removed + `lowercase`) and matched via `contains` (SearchViewModel.kt:54-59). Result: searching "الله" matches the fully-diacritized "اللّٰه" — diacritics-tolerant search is the intended behavior.

**Trade-off:** Tolerant and convenient but may return broader matches than the user's literal query.

**Follow-up:** Where would you store the search result if you wanted to restore it after closing the screen?

---

### Q15: Why does search exclude the 99 Names of Allah?
**Answer (≈35s):** `SearchViewModel` builds its search from **only 10 categories** (SearchViewModel.kt:34-45) — it deliberately excludes `assma_hussna`. The reason: the 99 Names list (216 texts in AssmaHussnaText/assmaHussnaList) is displayed in its educational nature via `AssmaHussnaViewModel` (AssmaHussnaViewModel.kt:7-12), not as searchable athkar; including them in search results would conflate concepts. This exclusion is explicitly declared.

**Trade-off:** Clarity of purpose (no educational content in search) vs. a user who might expect to find the Names in search results.

**Follow-up:** If you wanted to include them later, what is the minimum change in `SearchViewModel`?

---

### Q16: How is the text rendered for display and footer?
**Answer (≈30s):** In `AthkarTextSlider`, `duaText` is displayed in an Arabic font at a controllable size, then if `footer` exists it appears below it in a subordinate font and size (AthkarTextSlider.kt:106-125). Text is passed vertically within the page (verticalScroll) to adapt to length. Font is selected from `derivedStateOf` by name (AthkarTextSlider.kt:60-67). `duaText` may be null and is displayed as empty text safely (AthkarTextSlider.kt:107).

**Trade-off:** A single display pattern serves all categories vs. limited formatting flexibility per content type.

**Follow-up:** How do you handle text that is very long and may overflow a phone screen?

---

## Category 5 — Notifications & Background Work

### Q17: How are morning/evening notifications scheduled?
**Answer (≈40s):** `NotificationService` builds `PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)` with `setInitialDelay` to calculate the time until the requested hour, and `ExistingPeriodicWorkPolicy.UPDATE` (NotificationService.kt:42-49). The delay is calculated from `Calendar`: if today's time has passed, one day is added (NotificationService.kt:59-71). Jobs are enqueued with unique names via `enqueueUniquePeriodicWork` so duplicates are prevented (NotificationService.kt:47-48).

**Trade-off:** WorkManager manages scheduling and survives reboots vs. a minimum precision floor (no immediate notification).

**Follow-up:** Why 1 day and not weekly / every ~15 minutes?

---

### Q18: What happens in `ReminderWorker` when the job fires?
**Answer (≈35s):** It reads `type` from input data ("morning"/"evening"), and if missing returns `Result.failure()` (ReminderWorker.kt:18,30). It picks a random dhikr from the morning or evening list, takes the first 200 characters, and builds a `NotificationCompat.Builder` with `BigTextStyle`, default priority, and vibration (ReminderWorker.kt:17-45). It then calls `manager.notify` with an incrementing number via `AtomicInteger` (ReminderWorker.kt:44,49).

**Trade-off:** Random content increases variety vs. unpredictability of the specific text shown.

**Follow-up:** What if the list is empty or `duaText` is null? (Answer: A default fallback message, ReminderWorker.kt:21-22.)

---

### Q19: How are settings managed between the ViewModel and preferences?
**Answer (≈35s):** `NotificationSettingsViewModel` receives `SharedPrefsManager` and `NotificationService` injected (NotificationSettingsViewModel.kt:11-14). On toggle: it updates state, saves to preferences, then schedules or cancels (NotificationSettingsViewModel.kt:35-53). On time change: it saves then re-schedules *only if currently enabled* (NotificationSettingsViewModel.kt:56-76). `SharedPrefsManager` wraps 6 SharedPreferences keys (SharedPrefsManager.kt:37-45).

**Trade-off:** Synchronous writes reduce risk but `apply()` is non-blocking at the execution level.

**Follow-up:** If the user changes time, disables, then re-enables — is the schedule still correct?

---

### Q20: Does the app request notification permission at runtime?
**Answer (≈30s):** No. `POST_NOTIFICATIONS` is declared in the manifest only (AndroidManifest.xml:4) with no `requestPermissions` call anywhere in the code. On API 33+ devices, the permission must be manually granted via Settings or via adb (as shown in the demo script). This is a declared limitation — do not claim the app requests it at runtime.

**Trade-off:** Simplified flow (no permission dialog) vs. notifications that won't appear for some users until they grant the permission themselves.

**Follow-up:** If you decided to add a runtime permission request, where would you place it to not interrupt the reading experience?

---

## Category 6 — Performance & Offline

### Q21: How does the app handle performance with large lists like 216 Names of Allah?
**Answer (≈35s):** The view is designed for reading: `HorizontalPager` shows one page at a time and offloads the rest (AthkarTextSlider.kt:91-97) — no rendering or layout work for invisible pages. `AthkarItem` has a fixed `dataList` read from an in-memory object, so no network load or JSON parsing at runtime. The view does not display 216 items in a single `LazyColumn` — it uses a `pager` without a heavy scroll list.

**Trade-off:** One-page-at-a-time model is excellent for reading but not a "scrollable list" for quick browsing — hence the index drawer/slider.

**Follow-up:** Why is there no `LazyColumn` in the athkar screens? What mechanism moves between pages?

---

### Q22: What does "offline-first" actually mean here?
**Answer (≈35s):** The app relies on no network whatsoever: no network dependencies in `app/build.gradle.kts` (no Retrofit/OkHttp), and all data is local (embedded in code via data/text/ + SharedPreferences). It works fully offline from the first install (README.md:154-157). "Offline-first" here is not a caching strategy for later connectivity — there is no connectivity at all.

**Trade-off:** Maximum reliability and simplicity vs. no dynamic features/updates.

**Follow-up:** If you wanted to add cloud sync later, how would you build it without breaking the current design?

---

### Q23: Is there a performance issue from loading all texts at once?
**Answer (≈30s):** Not in practice: all texts are Kotlin constants loaded at Repository initialization as `AthkarItem` objects in memory (AthkarRepository.kt:8-538) — small text sizes. The app does not load JSON that incurs parsing cost each time; it builds the lists once. This is acceptable given the small size; if data grew to hundreds of megabytes lazy loading or SQLite would be needed.

**Trade-off:** Eager full load is simple (instant search across all categories, SearchViewModel.kt:34-45 requires all texts in memory) vs. always-occupied memory.

**Follow-up:** What consumes the most memory — the texts or the background images?

---

## Category 7 — Security & Threat Model

### Q24: What is the intended threat model for an app that never touches the network?
**Answer (≈40s):** Since there is no network, no accounts, and no sensitive user data, the attack surface is small. The remaining weak points: (1) **Notification data** — texts are published publicly; no confidentiality required. (2) **Text sharing** — `ShareUtil` opens a `<chooser>` for sharing (ShareUtil.kt:8-15); no major risk. (3) **External intents** — `WhatsAppUtil` opens an external URI with exception handling (WhatsAppUtil.kt:13-27). (4) **Backup** — `allowBackup="true"` in the manifest (AndroidManifest.xml:8). No keys or secrets are stored in code.

**Trade-off:** Not relying on the network shrinks the threat model automatically vs. no ability to update content or enforce server policies.

**Follow-up:** Do you encrypt any data when storing it in SharedPreferences?

---

### Q25: Why is there no login or accounts?
**Answer (≈35s):** The app is a local reading tool with no personal data; no authentication is needed. All state is local (counters, font) and is never sent to a server. Adding accounts would require network access (violating the offline rule) and backend infrastructure — outside the scope of a simple "athkar tool." There is no server, so there is nothing to authenticate, nothing to upload, and nothing to steal.

**Trade-off:** Privacy and speed without accounts vs. no cross-device sync or cloud backup.

**Follow-up:** How would you share the same settings (font/toggle) across two devices?

---

### Q26: What gaps have you explicitly acknowledged in the presentation?
**Answer (≈40s):** Four declared limitations: (1) **Notification permission** — declared without runtime request (AndroidManifest.xml:4). (2) **Content anomalies** — e.g. duplicate `TEXT_26` in the tasbih list (AthkarRepository.kt:113-121), carried over from the original Flutter port. (3) **Ephemerality** — counters and font are in memory only (FontViewModel.kt:10-14). (4) **Pre-existing lint issues** in `NotificationService.kt` that we chose to document rather than fix in this release (README/demo sheet). Acknowledging them openly builds more credibility than hiding them.

**Trade-off:** Transparency (integrity) vs. presenting weaknesses that may become follow-up questions — the intent is to be prepared for them.

**Follow-up:** Why did you decide to document the tasbih anomaly rather than fix it now?

---

## Category 8 — Testing & Observability

### Q27: What do the unit tests cover and why are ViewModels the focus?
**Answer (≈40s):** We have **52 unit tests across 8 files** (JUnit4 + MockK + Turbine + kotlinx-coroutines-test, app/build.gradle.kts:71-74). The focus is on ViewModel logic because it is the heaviest part of the business logic. The densest: `BaseAthkarViewModelTest` covers all counter paths (increment, reset, completion, vibration) via sub-classes (BaseAthkarViewModelTest.kt:30-33,41-230). Also `SearchViewModelTest` covers matching and clearing (SearchViewModelTest.kt:12-60), `NotificationSettingsViewModelTest` tests scheduling/cancellation via `mockk` mocks (NotificationSettingsViewModelTest.kt:17-119), and `FontViewModelTest` tests font ranges (FontViewModelTest.kt:8-47).

**Trade-off:** Fast JVM unit tests focus on logic vs. less coverage of the visible layer.

**Follow-up:** What is the difference between what `BaseAthkarViewModelTest` covers vs. what is not covered at the screen level?

---

### Q28: What is **NOT** tested? (Be honest)
**Answer (≈40s):** The coverage gaps: (1) **Athkar category ViewModels (11 screens):** AthkarSabah, AthkarMassa, AthkarAfterSalat, AthkarBeforeBed, Tasbih, Estigfar, Hamd, SalatAlaRasoul, DuaMenQuran, DuaMenSunnah, AssmaHussna — no direct tests; they merely configure `dataList`/`completionMessage`/`counterMode` on top of `BaseAthkarViewModel` (e.g. AthkarSabahViewModel.kt:7-12) and their tested behavior is via the base class itself. (2) **NotificationService and ReminderWorker** — untested (rely on Android/WorkManager; no androidTest units). (3) **SharedPreferences** — in `NotificationSettingsViewModelTest`, `SharedPrefsManager` is mocked (mockk), not real (NotificationSettingsViewModelTest.kt:17-18), so no test against the actual interface. (4) **No androidTest at all** (no app/src/androidTest directory) — no Compose UI/Screenshot tests.

**Trade-off:** JVM speed and simplicity vs. clear gaps in the Android/UI layer that must be disclosed.

**Follow-up:** What is the first catch-up test you would write — `ReminderWorker` or a Compose UI test?

---

### Q29: Is there an observability/logging/crash reporting system?
**Answer (≈30s):** There is no centralized observability: no Logcat logging, no crash reporting tool like Crashlytics (no dependencies). No `android.util.Log` calls of any kind in the source. This means debugging is done by reading exceptions at runtime and during builds manually — no data on field crashes. This limitation restricts knowledge of user problems post-release.

**Trade-off:** Simplicity (no extra dependencies/services) vs. blindness in production.

**Follow-up:** What is the minimum logging integration you would add to get crash reports without breaking the offline architecture?

---

## Category 9 — Trade-offs & Roadmap

### Q30: What deliberate "simpler over more comprehensive" choices have you made?
**Answer (≈45s):** Examples: (1) **Manual DI** instead of Hilt — visible and simpler (AppModule.kt:22-24). (2) **SharedPreferences** instead of Room/DataStore — 6 keys are enough (SharedPrefsManager.kt:37-45). (3) **Embedded content** instead of downloading — instant offline with no network. (4) **In-memory counters** instead of persisting — keeps StateFlow simple and consistent (FontViewModel.kt:10-14). (5) **Unified content model** `AthkarItem` with three fields for all categories (AthkarItem.kt:4-7) — traded custom formatting per type for a unified build.

**Trade-off:** Every trade-off sacrifices comprehensiveness for simplicity/maintainability given the app's size — and the most important thing is that these are intentional, documented choices, not oversights.

**Follow-up:** Which of these choices would you change first if user numbers grew significantly?

---

> **Claims to avoid:**
> - ✗ "We tested everything" — coverage is 52 unit tests on ViewModels/utilities/Repository only; no androidTest, no Compose UI, no NotificationService, no real SharedPreferences.
> - ✗ "No bugs" — based on limited tests; no claim of bug-free.
> - ✗ "Production ready" — lack of production observability (no log/crash reporting) and the notification permission limitation prevent this claim.
> - ✗ "Offline means fewer bugs" — offline provides *confidence*, but does not replace thorough testing.
