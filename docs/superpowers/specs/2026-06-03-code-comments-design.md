# Code Comments for Junior Devs

## Goal
Add concise, junior-friendly comments across Athkarix Android (Kotlin/Compose) so a new Kotlin developer can understand the project's architecture, data flow, and key patterns without prior context.

## Scope
- **42 files** (~3,000 lines of logic code) — the entire Kotlin source excluding:
  - `*Text.kt` (10 data files, auto-generated Arabic content + scholarly footnotes)
  - `AthkarRepository.kt` (static lists referencing the above)
  - `AssmaHussnaText.kt` (1,351-line legacy file, unused — JSON-based loader used instead)
  - `assets/json/*.json` (raw data, not code)

## Cross-references (avoid restating juniors/)

The full architectural explanations already exist in `docs/juniors/`. **Do not restate them in code comments** — that creates a second source of truth that drifts. Instead, every KDoc should point at the right doc for deeper reading:

| Concept taught in code | Canonical doc to link |
|---|---|
| `StateFlow` / `MutableStateFlow` / `asStateFlow()` | `docs/juniors/04-viewmodel-deep-dive.md` (Deep Dive: MutableStateFlow vs StateFlow) |
| `SharedFlow` (navigation events, haptics) | `docs/juniors/04-viewmodel-deep-dive.md` (Why SharedFlow for navigation?) |
| `sealed class ViewEvent` | `docs/juniors/04-viewmodel-deep-dive.md` (ViewEvent Sealed Class) |
| `BaseAthkarViewModel` pattern | `docs/juniors/04-viewmodel-deep-dive.md` (BaseAthkarViewModel — The Core Brain) |
| Manual `AppModule` DI | `docs/juniors/07-navigation-and-di.md` (Dependency Injection: AppModule) |
| `Routes` + `NavHost` | `docs/juniors/07-navigation-and-di.md` (Navigation: Routes) |
| `AthkarItem` / `AthkarRepository` | `docs/juniors/06-data-layer.md` |
| `AppTheme` / `AppColor` | `docs/juniors/05-ui-layer.md` (Theme) |
| MVVM layer boundaries | `docs/juniors/03-architecture-overview.md` |

KDoc format for cross-references (one line):

```kotlin
/** Shared logic for every athkar category. See docs/juniors/04-viewmodel-deep-dive.md. */
abstract class BaseAthkarViewModel : ViewModel()
```

## Commenting Strategy

### Level: File + Block
- **File-level KDoc** (`/** ... */`) on every class, object, interface, and top-level function.
  - One-sentence purpose statement.
  - One-clause role in the architecture (e.g., "Consumed by `AthkarScreen` via `AthkarSabahViewModel`").
  - **One cross-reference to the relevant juniors/ doc** (see table above).
- **Block-level comments** (`// — label —`) before each logical section within a file.
  - Separates init blocks, state declarations, event handling, lifecycle methods, etc.
  - Explains *why*, not *what* — the code already says *what*.

### Style Rules
1. **No inline comments** on individual lines (too noisy for a golden-dark-theme app).
2. **No line-level comments** in composable lambdas (quickly goes stale).
3. **KDoc for public API** (classes, functions, properties) — keep under 3 lines, with a `See docs/juniors/…` line if relevant.
4. **`//` for internal section dividers** — prefixed/suffixed with ` — ` for visual scanning.
5. **Junior-friendly tone** — define terms like "StateFlow", "ViewModel", "sealed class" in 1-2 words when first used in a file. Assume the reader knows basic Kotlin but not Android architecture.
6. **No comments in `build.gradle.kts`**, `AndroidManifest.xml`, or resource files.

### Priority Order (implementation)
1. Data layer — models, services
2. DI — AppModule
3. Navigation — Routes, NavGraph
4. Theme & utilities
5. ViewModels — BaseAthkarViewModel + all children
6. UI Components — reusable composables
7. Screens — Home, Athkar, AssmaHussna, Search, Settings
8. Entry points — AthkarixApp, MainActivity

### Verification
- `./gradlew assembleDebug` must succeed.
- No functional changes — comments only, zero logic touched.
- Every new KDoc must either define a new term or link to a juniors/ doc — never restate existing juniors/ content.
