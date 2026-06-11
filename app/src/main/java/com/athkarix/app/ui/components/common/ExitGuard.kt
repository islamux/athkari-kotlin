package com.athkarix.app.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.athkarix.app.ui.components.AlertExitApp

@Composable
fun ExitGuard(
    showDialog: Boolean,
    onRequestExit: () -> Unit,
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(enabled = true, onBack = onRequestExit)

    if (showDialog) {
        AlertExitApp(
            onConfirmExit = onConfirmExit,
            onDismiss = onDismiss,
        )
    }
}
