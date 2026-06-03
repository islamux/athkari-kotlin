# AGENTS.md — Athkarix Android (Kotlin Port)

## Project Overview
Native Android port of the Flutter Athkarix Islamic athkar/dua app.
Kotlin + Jetpack Compose. Golden dark theme. Tasbeeh counter.

## Commands
```bash
./gradlew assembleDebug          # build debug APK
./gradlew assembleRelease        # build release APK
./gradlew lint                   # run lint
./gradlew test                   # run unit tests
```

## Project Tracker
The command center is symlinked from the Flutter project:
- `cc get-project-status` — view tracker
- `ccui` — TUI dashboard

## Architecture
- MVVM: ViewModel + StateFlow + Jetpack Navigation Compose
- Manual DI via factory pattern
- JSON data loaded from assets/
- SharedPreferences for persistence
- WorkManager for notifications

## Source Layout
```
app/src/main/java/com/athkarix/app/
  AthkarixApp.kt              # Application class
  MainActivity.kt             # Entry point (setContent)
  navigation/
    AthkarixNavGraph.kt       # NavHost with 13 routes
    Routes.kt                 # Route constants
  ui/theme/
    AppColor.kt               # Color constants
    AppTheme.kt               # MaterialTheme (dark)
    FontScaleUtil.kt          # Font size scaling
  ui/screen/                  # Screen composables
  viewmodel/                  # ViewModels
  data/
    model/                    # Data models
    repository/               # Repositories
    json/                     # JSON assets
  util/                       # Utilities (share, diacritics)
```

## Phases
Phase 1: Project scaffold & theme ✅
Phase 2: Data layer (models, constants, repository)
Phase 3: Navigation & DI
Phase 4: Base ViewModel & shared components
Phase 5: Home screen
Phase 6: Athkar screens (11 categories)
Phase 7: Assma Hussna (99 Names)
Phase 8: Search functionality
Phase 9: Notifications
Phase 10: Polish & distribution
