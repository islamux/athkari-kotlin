package com.athkarix.app.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.theme.AppColor

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = item.duaText ?: "",
                    color = AppColor.primaryGold,
                    textAlign = TextAlign.Center,
                )
                if (!item.footer.isNullOrBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = item.footer,
                        color = AppColor.footer,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
