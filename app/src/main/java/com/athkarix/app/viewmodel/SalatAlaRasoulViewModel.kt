package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Salat ala Rasoul (blessings upon the Prophet) — 44 texts, each read once. */
class SalatAlaRasoulViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.salatAlaRasoulList

    override val maxPageCounters: List<Int> = List(dataList.size) { 1 }

    override val completionMessage: String = "أنهيت الصلاة على الرسول"
}
