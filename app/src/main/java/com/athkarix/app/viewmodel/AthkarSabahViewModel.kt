package com.athkarix.app.viewmodel

import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.repository.AthkarRepository

/** Morning athkar — 24 items with custom repetition counts (some recited 3×, 7×, 10×, 100×). */
class AthkarSabahViewModel : BaseAthkarViewModel() {

    override val dataList: List<AthkarItem> = AthkarRepository.athkarSabahList

    override val maxPageCounters: List<Int> = listOf(
        1, 1, 3, 1, 1, 1, 4, 1, 3, 7, 1, 1, 3, 3, 1, 1, 1, 100, 10, 100, 3, 1, 100, 10
    )

    override val completionMessage: String = "أنهيت أذكار الصباح !"
}
