# Running Athkarix Without Android Studio

## Prerequisites

- **JDK 17+** — verify: `java -version`
- **Android SDK** — see [`docs/flutter-kotlin-shared-sdk.md`](./flutter-kotlin-shared-sdk.md) for the full explanation. TL;DR: shared Flutter SDK, works fine.

  Current `~/.bash_exports`:
  ```bash
  export ANDROID_HOME=/media/islamux/Variety/flutter-sdk/Android/Sdk
  export ANDROID_SDK_ROOT=$ANDROID_HOME   # keep in sync with ANDROID_HOME
  export ANDROID_AVD_HOME=$HOME/.android  # AVD configs live here after AS update
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
  ```
- **Gradle wrapper** — included (`./gradlew`), no manual install needed
- **Device / Emulator** — connected via USB (debugging enabled) or running emulator

### Starting the Emulator

List available AVDs and start one:

```bash
$ANDROID_HOME/emulator/emulator -list-avds
$ANDROID_HOME/emulator/emulator -avd Pixel_10 -no-snapshot -netdelay none -netspeed full &
```

Wait for boot, then verify:

```bash
adb wait-for-device && adb devices
```

The `&` runs it in the background so the terminal stays usable. Close the emulator window or use `adb emu kill` to stop it.

> **Note:** If you updated Android Studio, new AVDs live in `~/.android/avd/`. The old location at `~/Ext4Free/.android/avd/` still works for existing AVDs.

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
- AVD/emulator configs are at `~/.android/avd/` (Android Studio) or `~/Ext4Free/.android/avd/` (legacy).
- First build downloads Gradle + dependencies — will take a few minutes.
- For release builds, set up a signing key in `app/build.gradle.kts`.
