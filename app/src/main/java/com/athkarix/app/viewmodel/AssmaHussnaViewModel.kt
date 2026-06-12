package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Displays 216 educational texts about Allah's 99 Names — one swipe per text. */
class AssmaHussnaViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.assmaHussnaList

    override val completionMessage: String = "انهيت قراءة اسماء الله الحسنى"
}
