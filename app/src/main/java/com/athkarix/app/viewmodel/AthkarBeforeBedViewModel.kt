package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

class AthkarBeforeBedViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarBeforeGoToBedList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة أذكار النوم !"
}
