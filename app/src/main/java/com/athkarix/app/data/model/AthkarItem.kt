package com.athkarix.app.data.model

/** A single athkar/dua entry — the core data unit passed from repository to ViewModel to UI. */
data class AthkarItem(
    val duaText: String?,
    val footer: String? = null,
)
