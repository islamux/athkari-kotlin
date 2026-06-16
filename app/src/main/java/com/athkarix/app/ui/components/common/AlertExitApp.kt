package com.athkarix.app.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun AlertExitApp(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = Color(AppColor.surface.value),
        titleContentColor = AppColor.primaryGold,
        textContentColor = AppColor.textSecondary,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "! تنبيه",
                color = AppColor.primaryGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = "هل أنهيت أذكارك؟",
                    color = AppColor.textSecondary,
                    fontSize = 16.sp,
                )
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth().height(0.5.dp).background(AppColor.darkGold))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmExit) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = AppColor.primaryGold,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text("نعم", color = AppColor.primaryGold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("لا", color = AppColor.textSecondary, fontSize = 14.sp)
            }
        },
    )
}
