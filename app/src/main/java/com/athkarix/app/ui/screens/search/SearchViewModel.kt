package com.athkarix.app.ui.screens.search

import androidx.lifecycle.ViewModel
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository
import com.athkarix.app.util.DiacriticUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Searches all 10 athkar categories by diacritic-insensitive text matching. */
class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results: StateFlow<List<SearchResult>> = _results.asStateFlow()

    /** A single matching athkar with its category label and original index. */
    data class SearchResult(
        val category: String,
        val categoryKey: String,
        val item: AthkarItem,
        val index: Int,
    )

    private data class SearchCategory(
        val name: String,
        val route: String,
        val list: List<AthkarItem>,
    )

    private val allCategories = listOf(
        SearchCategory("أذكار الصباح", "athkar_sabah", AthkarRepository.athkarSabahList),
        SearchCategory("أذكار المساء", "athkar_massa", AthkarRepository.athkarMassaList),
        SearchCategory("أذكار بعد الصلاة", "athkar_after_salat", AthkarRepository.athkarAfterSalatList),
        SearchCategory("أذكار النوم", "athkar_before_bed", AthkarRepository.athkarBeforeGoToBedList),
        SearchCategory("التسبيح", "tasbih", AthkarRepository.tasbihList),
        SearchCategory("الاستغفار", "estigfar", AthkarRepository.estigfarList),
        SearchCategory("الحمد", "hamd", AthkarRepository.hamdList),
        SearchCategory("الصلاة على الرسول", "salat_ala_rasoul", AthkarRepository.salatAlaRasoulList),
        SearchCategory("دعاء من القرآن", "duaa_quran", AthkarRepository.duaMenQuranList),
        SearchCategory("دعاء من السنة", "duaa_sunnah", AthkarRepository.duaMenSunnahList),
    )

    // — Perform search (strips diacritics for matching, keeps original in results) —
    fun search(q: String) {
        _query.value = q
        if (q.isBlank()) {
            _results.value = emptyList()
            return
        }
        val normalized = DiacriticUtil.remove(q).lowercase()
        val allResults = mutableListOf<SearchResult>()
        for (cat in allCategories) {
            cat.list.forEachIndexed { index, item ->
                val text = DiacriticUtil.remove(item.duaText ?: "").lowercase()
                if (text.contains(normalized)) {
                    allResults.add(
                        SearchResult(
                            category = cat.name,
                            categoryKey = cat.route,
                            item = item,
                            index = index,
                        )
                    )
                }
            }
        }
        _results.value = allResults
    }
}
