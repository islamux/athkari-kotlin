# Presentation Package — Athkarix Android (English)

Package summary: A 60-minute technical presentation for Athkarix Android (Kotlin + Compose, MVVM, offline-first). This is the **English variant** of the Arabic presentation package at `presentation/`.

---

## Package Contents

| File | Description |
|---|---|
| `en/slides.html` | Technical presentation — 29 slides in RTL Arabic format, golden dark theme, keyboard and touch navigation, overview panel, speaker notes, print styles |
| `en/demo-script.md` | Live demo scenario — 10 minutes + compressed 4-minute fallback plan |
| `en/qa-guide.md` | Q&A guide — 30 questions with answers in English |
| `en/README.md` | This file — instructions and verification |

> For the Arabic package, see `../slides.html`, `../demo-script.md`, and `../qa-guide.md`.

---

## How to Run

Open `en/slides.html` in any modern browser. No local server required.

- Works offline after first load (Google Fonts are cached).
- To print/export PDF: press `Ctrl+P` from within the presentation.

---

## Keyboard Shortcuts

| Key | Action |
|---|---|
| `N` / `→` | Next slide |
| `P` / `←` | Previous slide |
| `O` | Overview panel |
| `S` | Speaker Notes |
| `F` | Fullscreen |

---

## Order of Use During Presentation

1. **Technical presentation** (`en/slides.html`) — full presentation (29 slides, ~46 minutes)
2. **Live demo** (`en/demo-script.md`) — live walkthrough (~10 minutes)
3. **Q&A guide** (`en/qa-guide.md`) — question/answer table (~4 minutes)

---

## First-Time Checklist (60 minutes)

- [ ] Run the full presentation with timing (stopwatch/clock) at least once
- [ ] Confirm slide count matches the counter (shows `29/29` at bottom)
- [ ] Run the build and test commands:
  ```bash
  ./gradlew assembleDebug
  ./gradlew test
  adb install app/build/outputs/apk/debug/app-debug.apk
  ```
  Confirm build, tests, and install all succeed
- [ ] Test the compressed fallback demo plan (4 minutes) from `en/demo-script.md`
- [ ] Test keyboard navigation (→, ←, N, P, O, S, F)
- [ ] Test fullscreen mode
- [ ] Test speaker notes
- [ ] Test overview panel
- [ ] Test touch interaction (on device/emulator)
- [ ] Test print/PDF export via `Ctrl+P`
- [ ] Prepare contingency screenshots for the live demo (serves as fallback plan)

---

## Environment Requirements

| Component | Version |
|---|---|
| JDK | 17 |
| Android SDK | 34 |
| Device or emulator API | 24+ (API 33+ required to test notifications) |
| Browser | Modern Chrome/Firefox/Edge |

---

## Known Issues

- 4 pre-existing `NewApi` lint errors in `NotificationService.kt` — a build-level issue, not introduced by this package.
- No internet dependencies in the app (offline-first), but Google Fonts require connectivity on first load.
