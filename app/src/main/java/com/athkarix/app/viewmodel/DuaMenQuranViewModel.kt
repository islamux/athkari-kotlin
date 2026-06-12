package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Dua men quran (Quranic supplications) — 13 texts, each read once. */
class DuaMenQuranViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.duaMenQuranList

    override val completionMessage: String = "أنهيت قراءة أدعية القرآن"
}
