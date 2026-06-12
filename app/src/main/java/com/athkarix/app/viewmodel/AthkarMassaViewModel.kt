package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Athkar massa (afternoon prayers) — 24 texts, each read once. */
class AthkarMassaViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarMassaList

    override val completionMessage: String = "أنهيت قراءة أذكار المساء"
}
