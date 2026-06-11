package com.athkarix.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.athkarix.app.ui.theme.AppColor

/** Exit-confirmation dialog that uses the app's CustomButton for confirm/dismiss. */
@Composable
fun AlertExitApp(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = Color(AppColor.darkGold.value),
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        onDismissRequest = onDismiss,
        title = { Text("! تنبيه") },
        text = { Text("هل أنهيت أذكارك؟") },
        confirmButton = {
            CustomButton(
                icon = Icons.Default.ExitToApp,
                text = "نعم",
                onClick = onConfirmExit,
            )
        },
        dismissButton = {
            CustomButton(
                icon = Icons.Default.Clear,
                text = "لا",
                onClick = onDismiss,
            )
        }
    )
}
