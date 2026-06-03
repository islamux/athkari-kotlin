package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

class AthkarAfterSalatViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarAfterSalatList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت قراءة أذكار مابعد الصلاة"
}
