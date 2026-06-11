package com.athkarix.app.ui.screens.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.dua.DuaContent

/** Displays a single search result — the full dua text + footer with back navigation. */
@Composable
fun SearchResultScreen(
    item: AthkarItem,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AthkarixTopAppBar(onBack = onBack)
        },
        containerColor = Color.Black,
    ) { padding ->
        DuaContent(
            duaText = item.duaText,
            footer = item.footer,
            modifier = Modifier.padding(padding),
        )
    }
}
