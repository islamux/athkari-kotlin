# Athkarix Presentation Package — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the 4-file Arabic presentation package (`presentation/`) that proves code-level understanding of Athkarix Android to a technical review team.

**Architecture:** Self-contained hand-rolled HTML deck (no build step, no CDN/dependencies — mandated by the prompt), backed by Markdown guides. All content is Arabic with English technical terms; every claim carries a verified `file:line` reference.

**Spec:** `reusable-presentation-prompt.en.md` (the prompt template), with its web-centric sections adapted per user choices.

## Global Constraints

- Language: Arabic (RTL: `lang="ar" dir="rtl"`), technical terms in English
- Duration: 60 minutes → ~29 slides at ~2 min/slide
- Audience: Technical team verifying code understanding
- Output folder: `presentation/`
- Hydration section: Adapted to Compose equivalents (first composition vs recomposition, prefs-read timing, process death restoration)
- Branch: `docs/presentation-package` (created)
- **No commits/PR** unless user explicitly asks — files stay uncommitted for review
- Lint has pre-existing 4 NewApi errors in `NotificationService.kt` — flag only NEW issues
- All file:line references must be verified against actual source before adopting
- No marketing language without evidence from the project
- RTL throughout (`<div dir="rtl">`, correct `lang` attribute)

## 60-Minute Time Budget

| Section | Minutes |
|---|---|
| Opening (product + problem) | 5 |
| Thesis (governing engineering idea) | 4 |
| Decisions journey (why architecture changed) | 7 |
| Mental model (data flow diagram) | 4 |
| Product experience (what the user sees) | 5 |
| Technical deep dive (navigation/state/compose/data/notifications/theme/testing) | 13 |
| Live demo | 10 |
| Trade-offs (what we gained and what we paid) | 4 |
| Roadmap (risk-based priorities) | 3 |
| Closing + open questions | 5 |
| **Total** | **60** |

## Web→Compose Adaptation Map

| Prompt requires | Becomes |
|---|---|
| SSR server render | First composition — what it computes, what it can't read yet |
| HTML before JS | First frame before async state resolves |
| First client render / mismatch | Nondeterministic initial composition → flicker/clobber |
| Hydration = attach handlers | Recomposition + state hoisting; ViewModel init as deterministic source |
| `useEffect` after commit | `LaunchedEffect`/init side-effects; safe state update after |
| localStorage correct pattern | Prefs read once in `init()` → StateFlow (`NotificationSettingsViewModel.kt:16-32`) |
| PWA/offline exercise | Inherently-offline demo + WorkManager notification exercise |
| API/sync exercise | `adb shell dumpsys`/WorkInfo inspection of scheduled work |

## Deck Story (29 slides)

1-4. Opening: what Athkarix is, problem solved
5. Thesis: offline-first MVVM, repository single source of truth, zero network
6-9. Decisions: Flutter→Compose; manual DI vs Hilt; hardcoded data vs assets/JSON; WorkManager vs AlarmManager
10. Mental model: data-flow diagram
11-15. Product experience: home, athkar slider, tasbih, search, settings
16-25. Deep dive: navigation, state, Compose state (3 slides), data, text processing, notifications, theme/RTL/fonts, testing
26. Demo divider
27. Trade-offs
28. Roadmap
29. Closing

## File Structure

- `presentation/slides.html` — RTL deck, golden-dark identity, keyboard/touch nav
- `presentation/demo-script.md` — Arabic, 10-min demo with fallback
- `presentation/qa-guide.md` — Arabic, 30 Q&A grouped
- `presentation/README.md` — run instructions, rehearsal checklist

---

## Task 1: Branch setup + plan save + deck skeleton

**Files:**
- Create: `docs/superpowers/plans/2026-09-06-presentation-package.md` (this plan)
- Create: `presentation/slides.html` (skeleton with navigation infrastructure only — no content slides yet)

**Interfaces:**
- Consumes: all source facts from the exploration report
- Produces: runnable HTML skeleton with RTL, keyboard nav, notes, index, fullscreen, responsive, print styles

- [ ] **Step 1:** Write the plan file (this file — already done above)
- [ ] **Step 2:** Create `presentation/slides.html` skeleton:
  - RTL shell with `lang="ar" dir="rtl"`
  - Golden-dark color tokens from AppColor.kt (`#FFE082` on `#000`, `#FFBF00` amber, `#1A1A1A` surface, `#9C27B0` purple accent)
  - Display font: Amiri (bundled in res/font), body: Noto Naskh Arabic
  - Keyboard nav (←/→ or N/P, O for overview, F for fullscreen, S for speaker notes)
  - Slide index panel, dynamic numbering, touch swipe support
  - `prefers-reduced-motion`, responsive mobile, print styles
  - Placeholder slides matching the 29-slide structure (section headers only)
  - Code font fallback (monospace)
  - All buttons with `aria-label`
- [ ] **Step 3:** Open `slides.html` in browser via chrome-devtools MCP and verify: arrow keys next/prev, O index, F fullscreen, S notes toggle, slide count displayed, responsive

**Verify:** browser smoke test passes — all keyboard shortcuts work, slide count = 0 (placeholder), no JS errors in console

---

## Task 2: Slides 1-10 (opening → mental model)

**Files:**
- Modify: `presentation/slides.html` (add slides 1-10 content)

**Interfaces:**
- Consumes: Task 1 skeleton
- Produces: slides 1-10 with Arabic prose, verified `file:line` references, inline SVG data-flow diagram

- [ ] **Step 1:** Write slides 1-10 in Arabic prose per the deck story:
  1. Title slide: Athkarix Android — عرض تقني (60 دقيقة)
  2. Agenda / خريطة العرض (with time budget)
  3. Opening: ما هو Athkarix؟
  4. المشكلة: offline Arabic-first athkar access
  5. Thesis: الفكرة الحاكمة — offline-first MVVM
  6. Decision: Flutter → Kotlin/Compose port
  7. Decision: manual DI vs Hilt (`di/AppModule.kt:22-24`)
  8. Decision: hardcoded data — no assets folder, `data/text/` auto-generated (`AthkarRepository.kt:6`)
  9. Decision: WorkManager 1-day periodic (`NotificationService.kt:40-49`)
  10. Mental model: data-flow diagram (inline SVG)
- [ ] **Step 2:** Verify every `file:line` reference against source
- [ ] **Step 3:** Browser smoke test — slides render, RTL correct, Arabic renders

**Verify:** all 10 refs spot-checked, Arabic text renders, no console errors

---

## Task 3: Slides 11-25 (experience + deep dive)

**Files:**
- Modify: `presentation/slides.html` (add slides 11-25 content)

**Interfaces:**
- Consumes: Tasks 1-2
- Produces: slides 11-25 including the 3-slide Compose state adaptation section

- [ ] **Step 1:** Write slides 11-25:
  11. Home grid
  12. Athkar slider + counter modes + haptics
  13. Tasbih floating counter
  14. Diacritic-insensitive search
  15. Settings + notifications
  16. Navigation: 16 routes, typed args (`AthkarixNavGraph.kt:35-51`)
  17. State: 17 ViewModels, `BaseAthkarViewModel.incrementPageController()` (`:67-100`)
  18. Compose state pt1: first composition vs recomposition
  19. Compose state pt2: correct pattern (NotificationSettingsViewModel `:16-32`) + wrong pattern + consequences
  20. Compose state pt3: process death, counters NOT persisted, WorkManager survives reboot
  21. Data model `AthkarItem.kt:4-8`, AthkarRepository
  22. Text processing: DiacriticUtil regex (`:4-8`), search excludes assma_hussna
  23. Notifications: channel, ReminderWorker `:21-27`, no boot receiver
  24. Theme/RTL/fonts: AppColor values, forced RTL, Amiri↔NotoNaskh
  25. Testing: 52 tests matrix, covered/not covered
- [ ] **Step 2:** Verify all `file:line` references
- [ ] **Step 3:** Mark code examples as "snippet" where abbreviated

**Verify:** refs verified, snippet labels present, Arabic renders

---

## Task 4: Slides 26-29 + polish + numbering sync

**Files:**
- Modify: `presentation/slides.html` (add slides 26-29, polish, sync)

**Interfaces:**
- Consumes: Tasks 1-3
- Produces: complete deck with synced numbering, full visual identity

- [ ] **Step 1:** Write slides 26-29:
  26. Demo divider (points to demo-script.md)
  27. Trade-offs (gained vs paid, 2-column)
  28. Roadmap (risk-based: NewApi guard, runtime permission, counter persistence, assets migration, search coverage, UI tests)
  29. Closing: one sentence + open questions
- [ ] **Step 2:** Sync dynamic slide count ↔ numbering ↔ index
- [ ] **Step 3:** Full responsive/reduced-motion/print pass
- [ ] **Step 4:** Browser smoke test — all 29 slides, full keyboard map, print preview

**Verify:** 29 slides render, count matches index, keyboard map complete

---

## Task 5: demo-script.md

**Files:**
- Create: `presentation/demo-script.md`

**Interfaces:**
- Consumes: deck slides 26-27
- Produces: Arabic demo script, 10 min + 4-min fallback

- [ ] **Step 1:** Write Arabic demo script with segments:
  - Preflight commands (`./gradlew assembleDebug`, `adb install`)
  - Home + theme walkthrough
  - Athkar sabah AUTO_ADVANCE completion event
  - Tasbih INFINITE haptics
  - Search with/without tashkeel
  - Search result deep navigation
  - Notification toggle + time change
  - WorkManager evidence via adb
  - Each: "say this" + external evidence → internal `file:line`
  - Fallback plan (compressed 4 min + screenshots)
  - Environment prerequisites (device/emulator, API 33+ manual permission grant)
  - Do/don't-say lists
- [ ] **Step 2:** Verify every command exists in the project
- [ ] **Step 3:** Verify every `file:line` reference

**Verify:** all commands runnable, all refs verified, Arabic prose

---

## Task 6: qa-guide.md

**Files:**
- Create: `presentation/qa-guide.md`

**Interfaces:**
- Consumes: all source facts
- Produces: 30 Q&A in Arabic

- [ ] **Step 1:** Write 30 questions grouped in 9 categories (Arabic):
  - Architecture & system design
  - Compose state/recomposition (replacing SSR/Hydration)
  - Navigation & state management
  - Data pipeline & text processing
  - Notifications & background work
  - Performance & offline
  - Security & threat model
  - Testing & observability
  - Trade-offs & roadmap
- [ ] **Step 2:** Each: 30-60s speakable answer + `file:line` ref + trade-off + follow-up
- [ ] **Step 3:** Include mandated explicit questions (Compose equivalents of SSR questions, what was/wasn't tested, claims to avoid)
- [ ] **Step 4:** Verify all refs

**Verify:** 30 questions, every ref exists, each answer speakable in ≤60s

---

## Task 7: README.md

**Files:**
- Create: `presentation/README.md`

**Interfaces:**
- Consumes: all files in presentation/
- Produces: run instructions, rehearsal checklist

- [ ] **Step 1:** Write README with:
  - Package overview (4 files, responsibilities)
  - How to run (open slides.html in browser)
  - Slide shortcuts table
  - Order of use (slides → demo → Q&A)
  - 60-minute rehearsal checklist:
    - Run full deck against timer at least once
    - Verify slide count matches index
    - Run preflight commands (assembleDebug, test) and confirm pass
    - Test fallback demo plan
    - Test keyboard nav, fullscreen, notes, index, touch
    - Check print/PDF export
    - Prepare demo backup (screenshots)
  - Environmental requirements (JDK 17, SDK 34, device/emulator)
- [ ] **Step 2:** Verify all paths/links resolve

**Verify:** paths exist, checklist complete, honest environmental notes

---

## Task 8: Full verification + final report

**Files:**
- Modify: none (read-only verification)

**Interfaces:**
- Consumes: all 4 presentation/ files + source code
- Produces: final report

- [ ] **Step 1:** Browser smoke test of full deck (chrome-devtools): next/prev/notes/index/fullscreen/reduced-motion
- [ ] **Step 2:** Random 30% sample of all `file:line` refs across all 4 files — verify against source
- [ ] **Step 3:** Markdown fence balance check
- [ ] **Step 4:** `./gradlew test` — expect 52 passing
- [ ] **Step 5:** `./gradlew lint` — confirm only 4 pre-existing NewApi errors (no NEW issues)
- [ ] **Step 6:** `git diff --check` — confirm only `presentation/` + plan file added
- [ ] **Step 7:** Write final report: files created, visual decisions, Compose-state handling, command results, remaining warnings, branch name, no-commit confirmation

**Verify:** all commands pass, no new lint issues, report complete
