package com.athkarix.app.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun LoadingErrorContent(
    isLoading: Boolean,
    hasError: Boolean,
    errorMessage: String,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppColor.primaryGold,
                )
            }
            hasError -> {
                Column(Modifier.align(Alignment.Center)) {
                    Text(errorMessage, color = AppColor.textSecondary)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("إعادة المحاولة")
                    }
                }
            }
            else -> content()
        }
    }
}
