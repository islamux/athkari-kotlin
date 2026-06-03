package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

class AthkarMassaViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarMassaList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة أذكار المساء !"
}
