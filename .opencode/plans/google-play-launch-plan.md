# Google Play Store Launch Plan — أذكاري (Athkarix)

**Publisher:** islamux
**App:** أذكاري — Athkarix
**Version:** 1.0.0 (versionCode 1)
**Date:** 2026-06-16

---

## Phase 1: Privacy Policy

**Problem:** Play Store requires a Privacy Policy URL for apps that request permissions (`POST_NOTIFICATIONS`).

**Action:**
1. Go to **privacypolicyonline.com** or **app-privacy-policy-generator.firebaseapp.com**
2. Fill in:
   - App name: **أذكاري (Athkarix)**
   - Publisher: **islamux**
   - Data collected: **NONE** (check nothing)
   - Contact email: (use your email)
3. Generate → copy the hosted URL

**Deliverable:** https://www.termsfeed.com/live/a87e12f1-0420-48f3-a0c4-3b9a2a7a298d

---

## Phase 2: Generate Store Assets

### 2a — Screenshots (need 2–8, minimum 2)

**Capture instructions (Android emulator):**

```bash
# 1. List your AVDs
emulator -list-avds

# 2. Start a Pixel 7 or similar API 34 emulator
emulator -avd Pixel_7_API_34 &

# 3. Wait for boot, then install the debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 4. Take screenshots for each screen:
#    Home screen → tap any athkar → screenshot
#    Tasbeeh → screenshot
#    99 Names → screenshot
#    Search → screenshot
adb shell screencap /sdcard/screenshot_home.png
adb pull /sdcard/screenshot_home.png

# Repeat for each screen you want to capture
```

| # | Screen | Content |
|---|--------|---------|
| 1 | **Home screen** | 11 category buttons, gold-on-black |
| 2 | **Athkar counter** | E.g., Morning Athkar — shows dua text + count circles |
| 3 | **Tasbeeh** | Digital counter screen |
| 4 | **99 Names of Allah** | Scrolling list of names |
| 5 | **Search** | Search box with results populated |
| 6 | **Notification settings** | Morning/evening toggle switches |

**Requirements:**
- Aspect ratio: **16:9 or 9:16** (portrait)
- Resolution: min **1080×1920px**
- Format: **PNG or JPEG**

### 2b — Feature Graphic (1024×500px, required)

Use **Canva** (`canva.com`):
1. Search "Google Play feature graphic" template
2. Set background: black (#000000)
3. Add title: **أذكاري** (gold #C9A84C, large Arabic font)
4. Add subtitle: **Athkar & Duas — أذكار كل يوم** (white, smaller)
5. Export PNG at **1024×500px**

### 2c — App Icon
Already done: `res/mipmap-anydpi-v26/ic_launcher.xml` (black background, gold bead foreground).

---

## Phase 3: Store Listing

### App Details

| Field | Value |
|-------|-------|
| App name | **أذكاري — Athkarix** |
| Short description (80 chars) | أذكار وأدعية إسلامية لكل لحظة من يومك مع تسبيح وأسماء الله الحسنى |
| Full description (Arabic) | تطبيق أذكاري يقدم مجموعة شاملة من الأذكار والأدعية الإسلامية الواردة عن النبي محمد ﷺ. يتميز بواجهة مستخدم جميلة وسهلة، مع خلفية سوداء وذهبية فاخرة. المميزات: • ١١ قسمًا من الأذكار والأدعية (أذكار الصباح والمساء، أذكار بعد الصلاة، أذكار النوم، وغيرها) • عداد التسبيح الرقمي • البحث في جميع الأذكار (يدعم التشكيل) • أسماء الله الحسنى الـ ٩٩ مع شرح مختصر • تذكير بأذكار الصباح والمساء عبر الإشعارات • مشاركة الأذكار مع الآخرين |
| Full description (English) | Athkarix provides a complete collection of Islamic athkar (remembrances of God) and duas (supplications) from authentic sources, beautifully designed with a premium gold-on-black theme. Features include: • 11 categories of athkar (Morning, Evening, After Prayer, Before Bed, and more) • Digital tasbeeh counter with vibration feedback • Full-text search across all athkar (diacritic-insensitive) • 99 Names of Allah with explanations • Morning & evening notification reminders • Share athkar text with others • Elegant dark gold theme — easy on the eyes at night |
| Category | **Lifestyle** (or Education → Religion) |
| Tags | Islam, Athkar, Dua, Tasbeeh, أذكار, دعاء, تسبيح, أسماء الله الحسنى |

---

## Phase 4: Google Play Console Forms

### 4a — Data Safety

The app collects **NO data**:
- Location → No
- Personal info → No
- Financial info → No
- Health & fitness → No
- Messages → No
- Photos/videos → No
- Audio files → No
- Files & docs → No
- Calendar → No
- Contacts → No
- App activity → No
- App diagnostics → No
- Device IDs → No

**Data types shared:** None

**Security practices:** Toggle "Data can't be deleted" (all data is local on-device, no server).

### 4b — Content Rating

Complete IARC questionnaire:
- All content categories → "None"
- Expect **Everyone (E)** rating
- It's a purely religious/educational app with no mature content

---

## Phase 5: Build & Upload AAB

### 5a — Build Signed AAB
```bash
export ATHKARIX_STORE_PASSWORD="athkarix123"
export ATHKARIX_KEY_ALIAS="athkarix"
export ATHKARIX_KEY_PASSWORD="athkarix123"
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

### 5b — Upload to Play Console
1. Sign in to **play.google.com/console**
2. **Create app** → Name: "أذكاري" → Default language: Arabic (العربية) → App or game: App → Free
3. **Store listing** tab → fill all fields from Phase 3
4. Upload screenshots (Phase 2a) + feature graphic (Phase 2b)
5. Paste Privacy Policy URL (Phase 1)
6. **Data Safety** → complete form from Phase 4a
7. **Content Rating** → complete questionnaire (Phase 4b)
8. **Production → Create new release**:
   - Upload `app-release.aab`
   - Release name: "1.0.0"
   - Release notes (Arabic + English):
     > **العربية:** الإصدار الأول من تطبيق أذكاري. يحتوي على جميع الأذكار والأدعية اليومية مع عداد التسبيح وأسماء الله الحسنى.
     > **English:** First release of Athkarix. Includes daily athkar, duas, tasbeeh counter, and 99 Names of Allah.
9. **Start rollout to Production**

### 5c — Post-Submit
- Review takes **a few hours to 2 days** (first-time apps)
- Watch for rejection emails
- Common rejections: privacy policy issues, testing account issues
- If rejected → fix → re-upload

---

## Prerequisites Checklist
- [x] Google Play Developer account ($25 paid)
- [x] Privacy Policy URL generated (https://www.termsfeed.com/live/a87e12f1-0420-48f3-a0c4-3b9a2a7a298d)
- [ ] 6 screenshots captured (16:9 portrait, 1080×1920+)
- [ ] Feature graphic (1024×500 PNG)
- [ ] App descriptions written (Arabic + English)
- [ ] Signed AAB built (`./gradlew bundleRelease`)
- [ ] AAB uploaded to Play Console
- [ ] Release submitted for review

---

## Publisher Info
- **Name:** islamux
- **App:** أذكاري (Athkarix)
- **Contact:** (add your email here)

---

## Next Steps after This Plan
1. Execute Phase 1 (privacy policy) — 5 min
2. Execute Phase 2 (screenshots + feature graphic) — 30 min
3. Execute Phase 3 (store listing texts) — 15 min
4. Execute Phase 4 (forms in Play Console) — 20 min
5. Execute Phase 5 (build, upload, release) — 15 min
