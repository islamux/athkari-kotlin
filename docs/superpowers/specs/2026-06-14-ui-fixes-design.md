# UI Fixes & Enhancements Design

## Scope

Ten items from `test-my-ui.md`: background uniformity, navigation fixes, font system improvements, contact options, and floating counter corrections.

---

## 1. Background on All Screens

**Problem:** `BackgroundImage` (bg_home.jpg) is only used on `HomeScreen`. Other screens have solid black background.

**Solution:**
- Add `BackgroundImage()` as the base layer in `AthkarScreen`, `SearchScreen`, `NotificationSettingsScreen`.
- Apply a **full-screen black scrim** (`Color.Black.copy(alpha = 0.6f)`) between the background image and the content on every screen to guarantee text readability.
- The scrim can be a reusable wrapper composable or inlined per screen.

**Files affected:**
- `AthkarScreen.kt` — add BackgroundImage + scrim behind Scaffold
- `SearchScreen.kt` — add BackgroundImage + scrim behind Column
- `NotificationSettingsScreen.kt` — add BackgroundImage + scrim
- Optionally: extract a reusable `BackgroundScrim` composable in `BackgroundImage.kt`

---

## 2. Back Button on Search Page

**Problem:** `SearchScreen` has no navigation back button.

**Solution:**
- Add `onBack: () -> Unit` parameter to `SearchScreen`.
- Add `AthkarixTopAppBar` with back arrow above the `SearchTextField`.
- Pass `onBack = { navController.popBackStack() }` from `AthkarixNavGraph`.

**Files affected:**
- `SearchScreen.kt` — add TopAppBar with back arrow
- `AthkarixNavGraph.kt` — pass onBack handler

---

## 3. Bigger Home Button Labels

**Problem:** Category button text on home screen uses default font size (too small).

**Solution:**
- Add `fontSize = 18.sp` to the `Text` composable in `CustomButton`.
- Keep `fontFamily = FontFamily.SansSerif`.

**Files affected:**
- `CustomButton.kt` — add fontSize parameter (18sp)

---

## 4. Minus Icon for Decrease

**Problem:** Font controls use Clear/X icon for decrease instead of a minus sign.

**Solution:**
- Replace `Icons.Default.Clear` with `Icons.Default.Remove` (minus sign) in `FontControls.kt`.

**Files affected:**
- `FontControls.kt` — change icon import and usage

---

## 5. Font Overlap at Large Sizes

**Problem:** At ~34.6sp (3 increments from default 28.6) on Amiri font, Arabic text lines overlap vertically — line height is too tight.

**Solution:**
- Add explicit `lineHeight = (fontSize * 1.5f).sp` to the main `Text` composable in `AthkarTextSlider.kt`.

**Files affected:**
- `AthkarTextSlider.kt` — add lineHeight to main Text

---

## 6. Share "No Apps" Issue

**Status:** Not a bug — Waydroid environment limitation. No action needed.

---

## 7. Cairo Font Switching

**Problem:** FontViewModel switches between system Serif and SansSerif. Actual TTF fonts (Amiri, Cairo) exist in `res/font/` but are not wired.

**Solution:**
- Load Cairo as a proper `FontFamily` using `Font(R.font.cairo_regular)` and `Font(R.font.cairo_bold)`.
- Add `Font(R.font.amiri_regular)` and `Font(R.font.amiri_bold)` to replace the current system Serif fallback.
- Update `FontViewModel` to hold `FontFamily` objects instead of a string.
- Change `changeFont()` to toggle between Amiri and Cairo.
- Keep the existing font name as a label for display, but drive the actual rendering with `FontFamily` objects.
- Add a font toggle button in `FontControls` between the +/- buttons. Label shows current font name, tap toggles to the other.

**Files affected:**
- `FontViewModel.kt` — hold `FontFamily` objects + toggle logic
- `AthkarTextSlider.kt` — consume `FontFamily` directly from ViewModel
- `FontControls.kt` — add font-name toggle button
- `AppTheme.kt` — no changes needed

---

## 8. Contact Us with Email

**Problem:** Drawer only has WhatsApp contact. No email option.

**Solution:**
- Add an email contact item in `CustomDrawer` using `Intent.ACTION_SENDTO` with `mailto:fathi733@gmail.com`.
- Keep the WhatsApp item alongside. Label: "تواصل عبر البريد الإلكتروني" (Contact via email).

**Files affected:**
- `CustomDrawer.kt` — add email list item
- Inline email intent in `CustomDrawer`

---

## 9. Floating Counter for Sabah/Massa

**Problem:** `showFloatingCounter` is only true for Tasbih, Estigfar, Hamd, SalatAlaRasoul. Missing for Sabah and Massa.

**Solution:**
- In `AthkarixNavGraph`, set `showFloatingCounter = true` and pass `floatingCounterVM` for both `ATHKAR_SABAH` and `ATHKAR_MASSA` routes.

**Files affected:**
- `AthkarixNavGraph.kt` — update Sabah and Massa composable calls

---

## 10. Counter Per-Screen (Not Global)

**Problem:** `FloatingCounterViewModel` is singleton — counter state is shared across all screens. Tapping on Tasbih then navigating to Estigfar shows the same count.

**Solution (Option B — recommended):**
- Keep `FloatingCounterViewModel` as singleton but store counter per screen key in a `Map<String, Int>`:
  ```kotlin
  private val _counters = MutableStateFlow<Map<String, Int>>(emptyMap())
  fun getCounter(screenKey: String): StateFlow<Int>
  fun increment(screenKey: String)
  fun reset(screenKey: String)
  ```
- Each screen passes its route name as `screenKey`.
- This preserves counter state across navigation (user can switch screens and return to find their count intact).

**Files affected:**
- `FloatingCounterViewModel.kt` — refactor to map-based storage
- `AthkarixNavGraph.kt` — pass route key
- `AthkarScreen.kt` — use screenKey with floatingCounterVM calls

---

## Files Summary

| File | Changes |
|---|---|
| `BackgroundImage.kt` | Optionally add reusable scrim composable |
| `AthkarScreen.kt` | Add background + scrim; pass screenKey to floatingCounterVM |
| `SearchScreen.kt` | Add TopAppBar with back arrow, background + scrim |
| `NotificationSettingsScreen.kt` | Add background + scrim |
| `AthkarixNavGraph.kt` | Pass onBack to SearchScreen, floatingCounter to Sabah/Massa, pass route key |
| `CustomButton.kt` | Add fontSize = 18.sp |
| `CustomDrawer.kt` | Add email contact item |
| `FontControls.kt` | Change Clear to Remove icon |
| `AthkarTextSlider.kt` | Add lineHeight, consume FontFamily directly |
| `FontViewModel.kt` | Store FontFamily objects, load TTF fonts |
| `FloatingCounterViewModel.kt` | Map-based per-screen counter storage |
| `ShareUtil.kt` | No changes |
| `WhatsAppUtil.kt` | No changes |

## Non-goals

- No new screens or navigation routes
- No theming/color scheme changes
- No data persistence for counters or font settings
- No Hilt/Dagger or network dependencies
