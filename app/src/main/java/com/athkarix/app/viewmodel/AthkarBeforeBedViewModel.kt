package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Athkar before bed (night prayers) — 10 texts, each read once. */
class AthkarBeforeBedViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarBeforeGoToBedList

    override val completionMessage: String = "أنهيت قراءة أذكار قبل النوم"
}
