package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Athkar after salat (post-prayer prayers) — 12 texts, each read once. */
class AthkarAfterSalatViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarAfterSalatList

    override val completionMessage: String = "أنهيت قراءة رسائل أذكار ما بعد الصلاة"
}
