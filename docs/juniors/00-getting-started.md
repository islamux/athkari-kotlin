# Getting Started — Athkarix for Kotlin Juniors

## What Is This Project?

**Athkarix** (أذكاري) is an Android app for reading Islamic athkar (remembrances of God) and duas (supplications). It's a native Kotlin port of an existing Flutter app with the same name. The app features:

- 11 categories of athkar (morning, evening, after prayer, before bed, etc.)
- A tasbeeh (counting) counter for repetitive prayers
- The 99 Names of Allah (Al-Asma Al-Husna)
- Full-text search across all athkar
- Notification reminders for morning and evening athkar
- A dark gold-on-black theme

## What You Need to Know

### Kotlin Basics
If you're new to Kotlin, you only need to understand a few things to read this codebase:

- **Variables**: `val` (read-only, like `final` in Java) and `var` (mutable)
- **Functions**: declared with `fun`, e.g. `fun doSomething(): ReturnType`
- **Classes**: `class Name`, `data class Name` (auto-generates equals/hashCode/toString)
- **Objects**: `object Name` — a singleton (one instance, no `new` keyword needed)
- **Null safety**: `Type?` means the value CAN be null; `?.` is a safe call; `!!` asserts non-null

Start with the [official Kotlin tour](https://kotlinlang.org/docs/getting-started.html) — it takes about an hour.

### Android Basics
- **Activity**: A screen in Android. This app has ONE activity (`MainActivity`).
- **Compose**: Modern Android UI toolkit where you describe your UI with Kotlin functions instead of XML layouts.
- **ViewModel**: Holds UI state and survives screen rotations.
- **Gradle**: The build system. You don't need to understand it deeply, just know that `build.gradle.kts` files define dependencies and settings.

## Opening the Project

1. Install [Android Studio](https://developer.android.com/studio) (latest version)
2. Open Android Studio → "Open an existing project" → select the `Athkarix-android` folder
3. Wait for Gradle to sync (it downloads dependencies automatically)
4. You should see the project structure in the Project panel

## Building and Running

### With Android Studio
- Click the green ▶ "Run" button (select an emulator or connected device)
- Or: Build → Make Project (or Ctrl+F9)

### From the Command Line
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run lint checks
./gradlew lint

# Run unit tests
./gradlew test
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project Directory Structure

```
Athkarix-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/athkarix/app/   ← ALL Kotlin source code
│   │   │   ├── AthkarixApp.kt       ← Application class (app startup)
│   │   │   ├── MainActivity.kt      ← Entry point (launches the UI)
│   │   │   ├── di/                  ← Dependency injection (wires things together)
│   │   │   ├── navigation/          ← Screen routing
│   │   │   ├── ui/                  ← User interface (Compose screens & components)
│   │   │   ├── viewmodel/           ← ViewModels (state management)
│   │   │   ├── data/                ← Data layer (models, repository, text, services)
│   │   │   └── util/                ← Utilities (sharing, fonts, search helpers)
│   │   ├── assets/json/             ← JSON data (99 Names of Allah)
│   │   └── res/                     ← Resources (images, fonts, strings, theme)
│   └── build.gradle.kts             ← App-level build config
├── build.gradle.kts                 ← Root build config
├── settings.gradle.kts              ← Gradle settings
└── gradlew                          ← Gradle wrapper script
```

## First Things to Read

If you want to understand how the app works from end to end, read these files in order:

1. `MainActivity.kt` — Where the app starts
2. `ui/theme/AppTheme.kt` — The color scheme and typography
3. `navigation/AthkarixNavGraph.kt` — How all screens connect
4. `di/AppModule.kt` — How dependencies are wired
5. `viewmodel/BaseAthkarViewModel.kt` — The core "brain" of athkar reading
6. `ui/screens/home/HomeScreen.kt` — The main menu
7. `ui/screens/athkar/AthkarScreen.kt` — The athkar reader screen
8. `data/repository/AthkarRepository.kt` — Where all athkar data lives

## Key Gradle Config

- **compileSdk = 34** — targeting Android 14
- **minSdk = 24** — supports devices back to Android 7.0
- **Kotlin 2.2.10** + Jetpack Compose — modern stack
- No network libraries — everything is local
- No Hilt/Dagger — manual dependency injection (simple `object` pattern)

## Common Gradle Commands

```bash
./gradlew tasks                    # List all available tasks
./gradlew :app:dependencies        # Show dependency tree
./gradlew clean assembleDebug      # Clean build
```

## Next Docs

- `01-kotlin-concepts.md` — Every Kotlin feature used here, explained with real code
- `02-architecture-overview.md` — MVVM and data flow
- `03-viewmodel-deep-dive.md` — How ViewModels manage state
- `04-ui-layer.md` — Compose screens and components
- `05-data-layer.md` — Text data, JSON, preferences, services
- `06-navigation-and-di.md` — Screen routing and manual DI
