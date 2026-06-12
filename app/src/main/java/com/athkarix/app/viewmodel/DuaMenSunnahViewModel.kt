package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Dua men sunnah (prophetic supplications) — 46 texts, each read once. */
class DuaMenSunnahViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.duaMenSunnahList

    override val completionMessage: String = "أنهيت قراءة أدعية السنة"
}
