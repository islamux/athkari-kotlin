package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Salat alaa al rasoul (prayer for prophet) — 44 texts, infinite counter mode. */
class SalatAlaRasoulViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.salatAlaRasoulList
    override val completionMessage: String = "أنهيت قراءة رسائل صلاة الله عليه وسلم"
    override val counterMode: CounterMode = CounterMode.INFINITE_COUNT
}
