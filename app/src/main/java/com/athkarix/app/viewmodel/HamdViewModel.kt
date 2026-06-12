package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Hamd (praise of Allah) — 56 texts, each read once. */
class HamdViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.hamdList

    override val completionMessage: String = "أنهيت قراءة رسائل الحمد"
}
