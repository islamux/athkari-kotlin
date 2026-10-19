package com.athkarix.app.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.athkarix.app.R
import com.athkarix.app.ui.theme.AppColor

/** Custom exit confirmation dialog: gold icon badge, Amiri title, pill buttons. */
@Composable
fun AlertExitApp(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit,
) {
    val amiriBold = remember {
        FontFamily(Font(R.font.amiri_bold, weight = FontWeight.Bold))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AppColor.surface,
            border = BorderStroke(1.dp, AppColor.primaryGold.copy(alpha = 0.3f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(AppColor.primaryGold.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.TaskAlt,
                        contentDescription = null,
                        tint = AppColor.primaryGold,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "تنبيه",
                    color = AppColor.primaryGold,
                    fontFamily = amiriBold,
                    fontSize = 20.sp,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "هل أنهيت أذكارك؟",
                    color = AppColor.textSecondary,
                    fontSize = 16.sp,
                )

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, AppColor.primaryGold),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColor.primaryGold,
                    ),
                ) {
                    Text(
                        text = "لا، متابعة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onConfirmExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColor.primaryGold,
                        contentColor = AppColor.background,
                    ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "نعم، إنهاء",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
