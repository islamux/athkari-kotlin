# ViewModel Clean Code Guard Refactoring

## Before

Every category ViewModel duplicated boilerplate by overriding `maxPageCounters`:

```kotlin
class TasbihViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> = AthkarRepository.tasbihList
    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }
    override val completionMessage: String = "أنهيت قراءة رسائل التسبيح "
}
```

This pattern was repeated identically in **11 ViewModel files**, producing unnecessary code and complexity.

`BaseAthkarViewModel` carried `abstract val maxPageCounters: List<Int>` as a subclass contract, forcing every subclass to provide the same default value.

`AssmaHussnaViewModel.kt` had corrupted file structure with unintended duplicate class definitions and missing state declarations.

---

## After

Duplicated `override val maxPageCounters` removed from all 11 category ViewModels:

```kotlin
class TasbihViewModel : BaseAthkarViewModel() {
    override val dataList: List<AthkarItem> = AthkarRepository.tasbihList
    override val completionMessage: String = "أنهيت قراءة رسائل التسبيح "
}
```

`BaseAthkarViewModel` simplified:

- Removed `abstract val maxPageCounters` contract
- Hardcoded default `val max = 1` directly in counter logic
- Improved `getShareText` null safety

`AssmaHussnaViewModel.kt` restructured completely:

- Proper package declaration, imports, and class structure
- Added missing loading/error state declarations (`_isLoading`, `_hasError`, `_errorMessage`, `_dataList`)
- Removed duplicate function definitions

---

## Summary

| Metric | Before | After |
|--------|--------|-------|
| Files modified | — | 12 ViewModel files |
| Total lines removed | — | 45 |
| Overrides per ViewModel | 3 (`dataList` + `maxPageCounters` + `completionMessage`) | 2 (`dataList` + `completionMessage`) |
| `maxPageCounters` references | 12 | 0 |
| DRY violations | 11 | 0 |
| SRP violations | Yes | Addressed |
| YAGNI violations | Yes | Addressed |

---

## Principles Applied

- **SRP**: Single Responsibility — ViewModels answer to one actor
- **DRY**: Knowledge duplication eliminated
- **YAGNI**: Removed speculative configurability
- **Clean Code**: Intent-revealing names, proper null safety, no paraphrasing comments
