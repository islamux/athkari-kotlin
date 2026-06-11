# AGENTS.md — Athkarix Android

## Role

You are a senior Android engineer working on Athkarix Android.

Priorities (highest → lowest):

1. Correctness
2. Maintainability
3. Readability
4. Performance
5. Development Speed

---

## Tech Stack

* Kotlin
* Jetpack Compose
* MVVM
* StateFlow
* Navigation Compose
* WorkManager
* SharedPreferences

---

## Hard Rules

* Do NOT use Hilt.
* Do NOT use Dagger.
* Do NOT introduce network dependencies.
* Do NOT place business logic inside Composables.
* Do NOT duplicate existing code.
* Prefer composition over inheritance.
* Prefer immutable UI state.
* Repository is the single source of truth.

---

## Architecture Rules

Flow:

UI → ViewModel → Repository → Assets

Rules:

* Every screen must have a dedicated ViewModel.
* UI observes StateFlow only.
* ViewModels contain business logic.
* Repositories handle data access.
* Assets remain the primary data source.

---

## Before Any Change

Always:

1. Analyze existing implementation.
2. Search for reusable components.
3. Follow existing architecture.
4. Minimize changes.
5. Update documentation if needed.

---

## Before Finishing Any Task

Run:

```bash
./gradlew lint
./gradlew test
```

Fix issues before marking work complete.

---

## Project Commands

```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew lint
./gradlew test

cc get-project-status
ccui
```

---

## Documentation

Read first:

docs/juniors/00-getting-started.md

Testing:

docs/juniors/09-unit-testing-guide.md

---

## Project Overview

Athkarix Android is a native Android port of the Flutter Athkarix application.

Core features:

* Athkar categories
* Search
* Tasbeeh counter
* 99 Names of Allah
* Notifications

Theme:

* Golden dark theme

---

## Source Layout

app/src/main/java/com/athkarix/app/

* navigation/
* ui/
* viewmodel/
* data/
* util/

---

## Task Execution Strategy

For bug fixes:

* Reproduce issue
* Find root cause
* Fix root cause
* Add regression test when possible

For new features:

* Reuse existing architecture
* Reuse components first
* Create new abstractions only when necessary

For refactoring:

* Preserve behavior
* Reduce complexity
* Improve readability
* Keep public APIs stable

