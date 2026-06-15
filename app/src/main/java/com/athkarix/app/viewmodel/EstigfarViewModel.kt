package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Istighfar (seeking forgiveness) — 25 supplications, infinite counter mode. */
class EstigfarViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.estigfarList
    override val completionMessage: String = "أنهيت قراءة رسائل الإإستغفار "
    override val counterMode: CounterMode = CounterMode.INFINITE_COUNT
}
