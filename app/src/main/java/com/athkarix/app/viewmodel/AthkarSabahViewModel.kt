package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Athkar sabah (morning prayers) — 33 texts, each read once. */
class AthkarSabahViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarSabahList

    override val completionMessage: String = "أنهيت قراءة أذكار الصباح"
}
