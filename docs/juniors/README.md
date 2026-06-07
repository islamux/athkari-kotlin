# `docs/juniors/` — Q&A Index

> **Not sure where to look?** Start here. Each question points to **one** canonical doc. Every other doc links back to it rather than restating the content.

## Core questions

| I want to understand… | Read this |
|---|---|
| What is this project, how do I open it, what's the file layout, and what's the first file I should read? | [`00-getting-started.md`](./00-getting-started.md) |
| What Kotlin features does the codebase use (`val`, `data class`, `sealed class`, `object`, `StateFlow`, coroutines, lambdas, scope functions, extension functions, `remember`)? | [`01-kotlin-concepts.md`](./01-kotlin-concepts.md) |
| I'm coming from Flutter. How do Flutter concepts map to Jetpack Compose? | [`02-flutter-to-compose.md`](./02-flutter-to-compose.md) |
| How does MVVM work here? What's the big picture, and what's the strict data flow direction? | [`03-architecture-overview.md`](./03-architecture-overview.md) |
| How does a ViewModel hold state and emit events? Why `StateFlow` for some, `SharedFlow` for others? What's the private-mutable / public-readonly split? | [`04-viewmodel-deep-dive.md`](./04-viewmodel-deep-dive.md) |
| How is the UI built? What are the screens, the theme, the components, and how do they consume ViewModel state? | [`05-ui-layer.md`](./05-ui-layer.md) |
| Where does the data come from? `AthkarItem`, `AthkarRepository`, JSON assets, `SharedPreferences`? | [`06-data-layer.md`](./06-data-layer.md) |
| How does navigation work? Why a single Activity? What is `AppModule` and why no Hilt? | [`07-navigation-and-di.md`](./07-navigation-and-di.md) |
| What's the current state of the codebase? Known bugs, performance issues, dead code, over-engineering? | [`08-project-audit.md`](./08-project-audit.md) |
| How do I write a local JVM unit test (no emulator)? | [`09-unit-testing-guide.md`](./09-unit-testing-guide.md) |

## Canonical homes for re-explained topics

Some topics appeared in multiple docs. To prevent drift, each now has exactly one deep-dive home:

| Topic | Canonical home |
|---|---|
| `StateFlow` / `MutableStateFlow` / `asStateFlow()` | `04-viewmodel-deep-dive.md` (Deep Dive: MutableStateFlow vs StateFlow) |
| `SharedFlow` for one-shot events (navigation, haptics) | `04-viewmodel-deep-dive.md` (Why SharedFlow for navigation?) |
| `BaseAthkarViewModel` + the 11 subclasses | `04-viewmodel-deep-dive.md` (BaseAthkarViewModel — The Core Brain) |
| Manual `AppModule` DI (singleton vs fresh, no-Hilt rationale, Flutter/Cubit comparison) | `07-navigation-and-di.md` (Dependency Injection: AppModule) |
| `Routes` + `NavHost` + `navArgument` + single-Activity | `07-navigation-and-di.md` (Navigation: Routes) |
| `AppColor` / `AppTheme` (golden dark theme) | `05-ui-layer.md` (Theme) |
| Build / install / lint / test commands | `../run-without-studio.md` |
| Project directory structure | `00-getting-started.md` (Project Directory Structure) |

## Adding a new doc

If a topic doesn't fit any existing file:

1. Decide which of the 10 files (00–09) is its canonical home based on the table above.
2. If you must add a new file, number it `10-…` and add a row to the **Core questions** table above.
3. If you find yourself restating a topic that already has a canonical home, replace your restatement with a one-line cross-reference link.
