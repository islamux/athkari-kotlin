package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Tasbih (glorification of Allah) — 35 reflection texts, infinite counter mode. */
class TasbihViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.tasbihList
    override val completionMessage: String = "أنهيت قراءة رسائل التسبيح "
    override val counterMode: CounterMode = CounterMode.INFINITE_COUNT
}
