package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

class EstigfarViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.estigfarList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة رسائل الإإستغفار "
}
