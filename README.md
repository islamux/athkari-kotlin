<div dir="rtl">

# أذكاري

تطبيق أندرويد أصلي لقراءة الأذكار والأدعية الإسلامية. نسخة Kotlin + Jetpack Compose مقتبسة من تطبيق Athkarix الأصلي المبني بـ Flutter.

## المميزات

- ١١ تصنيفًا من الأذكار والأدعية (الصباح، المساء، بعد الصلاة، قبل النوم، وغيرها)
- عداد تسبيح مع نافذة عائمة
- أسماء الله الحسنى التسعة والتسعون مع معانيها
- بحث نصي شامل في جميع الأذكار
- إشعارات تذكير لأذكار الصباح والمساء
- واجهة داكنة بالذهب والأسود
- يعمل بالكامل بدون إنترنت
- يدعم اللغة العربية والاتجاه من اليمين لليسار

## التقنيات المستخدمة

| المكون | الاختيار |
|---|---|
| اللغة | Kotlin 2.2.10 |
| واجهة المستخدم | Jetpack Compose (BOM 2024.01.00) + Material 3 |
| البنية المعمارية | MVVM + StateFlow |
| التنقل | Navigation Compose 2.7.6 |
| المهام الخلفية | WorkManager 2.9.0 |
| حقن التبعيات | يدوي (بدون Hilt / Dagger) |
| مصدر البيانات | نصوص مضمنة + JSON |
| الإعدادات | SharedPreferences |
| الحد الأدنى / المستهدف لنظام SDK | 24 / 34 |
| الاختبارات | JUnit 4, MockK, Turbine, kotlinx-coroutines-test |

## البنية المعمارية

```
واجهة المستخدم ← ViewModel ← Repository ← البيانات / الإعدادات
```

- **نشاط واحد** يستضيف جميع الشاشات عبر Navigation Compose.
- لكل شاشة **ViewModel** خاص بها يصدّر `StateFlow<UiState>`.
- **Repository** هو المصدر الوحيد الموثوق لبيانات الأذكار.
- جميع البيانات محلية — لا توجد مكتبات شبكة.

## البدء

### المتطلبات

- Android Studio Ladybug (2024.1.1+) أو IntelliJ IDEA
- JDK 17
- Android SDK 34

### البناء

```bash
git clone https://github.com/your-org/athkarix-android.git
cd athkarix-android

# بناء نسخة التصحيح
./gradlew assembleDebug

# بناء نسخة الإصدار (يتطلب بيانات التوقيع)
./gradlew assembleRelease

# تشغيل التحليل البرمجي
./gradlew lint

# تشغيل الاختبارات
./gradlew test
```

> راجع `docs/run-without-studio.md` لإعداد البناء عبر سطر الأوامر فقط.

## هيكل المشروع

```
app/src/main/java/com/athkarix/app/
├── AthkarixApp.kt             # كلاس التطبيق
├── MainActivity.kt            # نقطة الدخول
├── data/
│   ├── local/                 # SharedPrefsManager
│   ├── model/                 # نموذج AthkarItem
│   ├── repository/            # AthkarRepository
│   ├── service/               # NotificationService, ReminderWorker
│   └── text/                  # ١١ ملفًا نصيًا للأذكار
├── di/                        # حقن التبعيات اليدوي
├── navigation/                # AthkarixNavGraph
├── ui/
│   ├── components/            # مكونات قابلة لإعادة الاستخدام
│   ├── screens/               # شاشات التطبيق
│   └── theme/                 # AppColor, AppTheme
├── util/                      # أدوات متنوعة
└── viewmodel/                 # ١٥ ViewModel
```

## التوثيق

أدلة المطورين موجودة في `docs/juniors/`:

| المستند | الموضوع |
|---|---|
| `00-getting-started.md` | نظرة عامة وإعداد المشروع |
| `01-kotlin-concepts.md` | مفاهيم Kotlin المستخدمة |
| `03-architecture-overview.md` | MVVM وتدفق البيانات |
| `04-viewmodel-deep-dive.md` | StateFlow و ViewModels |
| `05-ui-layer.md` | شاشات Compuse والسمات |
| `06-data-layer.md` | مصادر البيانات والخدمات |
| `07-navigation-and-di.md` | التنقل وحقن التبعيات |
| `09-unit-testing-guide.md` | اختبار الوحدة |

## الترخيص

[رخصة GNU العامة الإصدار 3.0](LICENSE)

---

</div>

# أذكاري — Athkarix

**Athkarix** is a native Android application for reading Islamic *athkar* (remembrances of Allah) and *duas* (supplications). It is a Kotlin + Jetpack Compose port of the original Flutter Athkarix app.

## Features

- 11 categories of athkar and duas (morning, evening, after prayer, before bed, etc.)
- Tasbeeh (counting) counter with a floating overlay
- 99 Names of Allah (Al-Asma Al-Husna) with meanings
- Full-text search across all athkar
- Notification reminders for morning and evening athkar
- Dark gold-on-black theme
- Fully offline — no network required
- Arabic-first with RTL layout

## Tech Stack

| Component | Choice |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2024.01.00) + Material 3 |
| Architecture | MVVM + StateFlow |
| Navigation | Navigation Compose 2.7.6 |
| Background work | WorkManager 2.9.0 |
| DI | Manual (no Hilt / Dagger) |
| Data source | Embedded text in source + JSON |
| Preferences | SharedPreferences |
| Min / Target SDK | 24 / 34 |
| Testing | JUnit 4, MockK, Turbine, kotlinx-coroutines-test |

## Architecture

```
UI (Compose) → ViewModel → Repository → Assets / Preferences
```

- **Single Activity** (`MainActivity`) hosts all screens via Navigation Compose.
- Each screen has a dedicated **ViewModel** exposing `StateFlow<UiState>`.
- **Repository** is the single source of truth for athkar data.
- All data is local — no network libraries are used.

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.1.1+) or IntelliJ IDEA
- JDK 17
- Android SDK 34

### Building

```bash
git clone https://github.com/your-org/athkarix-android.git
cd athkarix-android

./gradlew assembleDebug

./gradlew assembleRelease

./gradlew lint

./gradlew test
```

> See `docs/run-without-studio.md` for CLI-only setup instructions.

## Project Structure

```
app/src/main/java/com/athkarix/app/
├── AthkarixApp.kt             # Application class
├── MainActivity.kt            # Single entry point
├── data/
│   ├── local/                 # SharedPrefsManager
│   ├── model/                 # AthkarItem data model
│   ├── repository/            # AthkarRepository
│   ├── service/               # NotificationService, ReminderWorker
│   └── text/                  # 11 athkar category text files
├── di/                        # Manual DI (AppModule)
├── navigation/                # AthkarixNavGraph
├── ui/
│   ├── components/            # Reusable composables
│   ├── screens/               # Screen composables
│   └── theme/                 # AppColor, AppTheme
├── util/                      # DiacriticUtil, FontScaleUtil, etc.
└── viewmodel/                 # 15 ViewModels
```

## Documentation

Detailed developer guides are in `docs/juniors/`:

| Doc | Topic |
|---|---|
| `00-getting-started.md` | Project overview and setup |
| `01-kotlin-concepts.md` | Kotlin features used |
| `03-architecture-overview.md` | MVVM and data flow |
| `04-viewmodel-deep-dive.md` | StateFlow and ViewModels |
| `05-ui-layer.md` | Compose screens and theming |
| `06-data-layer.md` | Data sources and services |
| `07-navigation-and-di.md` | Routing and DI wiring |
| `09-unit-testing-guide.md` | Unit testing approach |

## License

[GNU General Public License v3.0](LICENSE)
