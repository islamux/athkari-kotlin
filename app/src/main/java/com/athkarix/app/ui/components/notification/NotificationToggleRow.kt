package com.athkarix.app.ui.components.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun NotificationToggleRow(
    label: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    timeText: String?,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = AppColor.textPrimary)
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
        )
    }
    if (enabled && timeText != null) {
        TextButton(
            onClick = onTimeClick,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(timeText, color = AppColor.primaryGold)
        }
    }
    Spacer(Modifier.height(8.dp))
}
