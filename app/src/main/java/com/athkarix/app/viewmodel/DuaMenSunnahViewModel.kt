package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Sunnah supplications — 44 duas from the Prophet's teachings, each read once. */
class DuaMenSunnahViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.duaMenSunnahList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة أدعية من السنة !"
}
