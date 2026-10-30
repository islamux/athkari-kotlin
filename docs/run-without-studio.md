# Running Athkarix Without Android Studio

## Prerequisites

- **JDK** — This project needs a JDK available. On this machine we use JetBrains JBR 21 from `/mnt/Ext4_Free/jdks/jetbrains_s_r_o_-21-amd64-linux.2`. Verify with `java -version`.
- **Android SDK** — See [`docs/flutter-kotlin-shared-sdk.md`](./flutter-kotlin-shared-sdk.md) for context. On this machine the SDK lives at `/mnt/Ext4_Free/Android/Sdk`.
- **Gradle wrapper** — Included (`./gradlew`), no manual install needed.
- **Device / Emulator** — Connected via USB (debugging enabled) or running emulator.

## Quick Start (This Environment)

These commands are verified to work on this setup. Run them from the project root.

### 1) Set environment variables

```bash
export ANDROID_HOME=/mnt/Ext4_Free/Android/Sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

export JAVA_HOME=/mnt/Ext4_Free/jdks/jetbrains_s_r_o_-21-amd64-linux.2
export PATH=$JAVA_HOME/bin:$PATH
```

### 2) Start the emulator (if not already running)

List available AVDs:
```bash
emulator -list-avds
```

Start the `flutter_emulator` AVD in background:
```bash
emulator -avd flutter_emulator -no-snapshot-load -no-boot-anim -gpu swiftshader_indirect &
```

Wait for it to fully boot:
```bash
adb wait-for-device
until [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
  echo "Waiting for emulator..."
  sleep 2
done
echo "Emulator ready"
```

Verify connection:
```bash
adb devices
```

### 3) Build, install and run

From `/mnt/Variety/Kotlin_Java/Kotlin_Projects/Athkarix-android`:
```bash
./gradlew installDebug
adb shell am start -n com.athkarix.app/.MainActivity
```

Package: `com.athkarix.app` | Activity: `.MainActivity`

## Generic Steps (Other Machines)

If you're on a different machine with dotfiles set up, you may be able to rely on `~/.bash_exports`. Reload and verify:

```bash
source ~/.bashrc
adb devices
```

Then:
```bash
./gradlew installDebug
adb shell am start -n com.athkarix.app/.MainActivity
```

To start an emulator generically:
```bash
emulator -avd <AVD_NAME> -no-snapshot -netdelay none -netspeed full &
adb wait-for-device && adb devices
```

> **Note:** If you updated Android Studio, new AVDs live in `~/.android/avd/`. Legacy AVDs may live in `~/Ext4Free/.android/avd/`.

## Common Tasks

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build APK only (no install) |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew installDebug` | Build and install debug APK on connected device/emulator |
| `./gradlew lint` | Run lint checks |
| `./gradlew test` | Run unit tests |

## Notes

- SDK path is set in `local.properties` (if present).
- Gradle cache goes to `$GRADLE_USER_HOME` (often set via dotfiles).
- First build downloads Gradle + dependencies — it may take several minutes.
- For release builds, configure signing in `app/build.gradle.kts`.
- If `installDebug` fails with `No connected devices!`, ensure the emulator is fully booted (`sys.boot_completed` = `1`) before installing.
