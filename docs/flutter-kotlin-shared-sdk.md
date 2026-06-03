# Android SDK Setup Explained

## Why two SDKs?

You have two Android SDK installations:

| Path | Source | Ownership |
|------|--------|-----------|
| `/media/islamux/Variety/flutter-sdk/Android/Sdk` | Installed by Flutter | `root` |
| `~/Ext4Free/Android/Sdk` | Standalone (via SDK Manager) | `you` |

Flutter installs its own SDK copy when you run `flutter doctor --android-licenses`. The standalone one you likely installed manually later via `sdkmanager`.

## Which one does what?

- **Flutter projects** use the Flutter-bundled SDK (`/media/islamux/Variety/flutter-sdk/Android/Sdk`).
- **Kotlin/Android projects** use whatever `local.properties` or `$ANDROID_HOME` points to.

Currently both point to the Flutter-bundled SDK:
- `local.properties` → `sdk.dir=/media/islamux/Variety/flutter-sdk/Android/Sdk`
- `$ANDROID_HOME` → `/media/islamux/Variety/flutter-sdk/Android/Sdk`

## Is it OK to share? Yes.

The Android SDK is just a collection of tools (build-tools, platforms, platform-tools, etc.). **There is nothing Flutter-specific about it.** Flutter uses the same Android SDK that any Kotlin/Java project uses — same `aapt2`, `d8`, `adb`, same API level platforms.

Sharing is:
- ✅ Normal — many frameworks (Flutter, React Native, Cordova) all reuse the same SDK
- ✅ Space-efficient — saves ~2GB by not duplicating
- ⚠️ The Flutter copy is **root-owned**, so `sdkmanager --install` requires `sudo`

Switch only if you need to modify SDK contents (install new platforms, etc.) without root. Otherwise it works fine as-is.

## Recommendation

**Keep using the Flutter-bundled SDK** for now — it's more complete (has build-tools 36.1.0, android-36) and already wired up. If you ever need to install/remove SDK packages often, switch `$ANDROID_HOME` and `local.properties` to your user-owned `~/Ext4Free/Android/Sdk`.
