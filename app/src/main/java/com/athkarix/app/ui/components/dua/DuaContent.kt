package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun DuaContent(
    duaText: String?,
    footer: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = duaText ?: "",
                color = AppColor.primaryGold,
                textAlign = TextAlign.Center,
            )
            if (!footer.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = footer,
                    color = AppColor.footer,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
