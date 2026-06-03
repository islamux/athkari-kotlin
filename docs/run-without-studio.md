# Running Athkarix Without Android Studio

## Prerequisites

- **JDK 17+** — verify: `java -version`
- **Android SDK** — see [`docs/flutter-kotlin-shared-sdk.md`](./flutter-kotlin-shared-sdk.md) for the full explanation. TL;DR: shared Flutter SDK, works fine.

  Current `~/.bash_exports`:
  ```bash
  export ANDROID_HOME=/media/islamux/Variety/flutter-sdk/Android/Sdk
  ```
  Ensure SDK tools are on PATH (add to `~/.bash_exports` if missing):
  ```bash
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
  ```
- **Gradle wrapper** — included (`./gradlew`), no manual install needed
- **Device / Emulator** — connected via USB (debugging enabled) or running emulator

## Steps

### 1. Reload dotfiles (if you edited `~/.bash_exports`)

```bash
source ~/.bashrc
```

### 2. Verify environment

```bash
adb devices          # should list your device/emulator
```

### 3. Build and install

```bash
./gradlew installDebug
```

This compiles, packages, and installs the debug APK on the connected device.

### 4. Run the app

```bash
adb shell am start -n com.athkarix.app/.MainActivity
```

Or simply tap the app icon.

## Common Tasks

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build APK only (no install) |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew lint` | Run lint checks |
| `./gradlew test` | Run unit tests |

## Switching SDK (optional)

See [`docs/flutter-kotlin-shared-sdk.md`](./flutter-kotlin-shared-sdk.md) if you want to switch to the standalone SDK.

## Notes

- SDK path is set in `local.properties`.
- Gradle cache goes to `$GRADLE_USER_HOME` (set to `~/Ext4Free` in your dotfiles).
- AVD/emulator configs are at `~/Ext4Free/.android/avd/`.
- First build downloads Gradle + dependencies — will take a few minutes.
- For release builds, set up a signing key in `app/build.gradle.kts`.
