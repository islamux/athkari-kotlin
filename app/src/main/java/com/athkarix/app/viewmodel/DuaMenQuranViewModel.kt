package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Quranic supplications — 12 duas from the Qur'an, each read once. */
class DuaMenQuranViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.duaMenQuranList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة أدعية من القراءن !"
}
