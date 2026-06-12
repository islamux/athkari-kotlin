package com.athkarix.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.notification.NotificationToggleRow
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.NotificationSettingsViewModel

/** Settings screen for toggling morning/evening reminder notifications and viewing scheduled times. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel,
    onBack: () -> Unit,
) {
    val morningEnabled by viewModel.morningEnabled.collectAsState()
    val eveningEnabled by viewModel.eveningEnabled.collectAsState()

    Scaffold(
        topBar = {
            AthkarixTopAppBar(
                title = "إعدادات التذكير",
                onBack = onBack,
            )
        },
        containerColor = Color.Black,
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            NotificationToggleRow(
                label = "تذكير الصباح",
                enabled = morningEnabled,
                onEnabledChange = { viewModel.setMorningEnabled(it) },
                timeText = "${viewModel.morningHour.value}:${String.format("%02d", viewModel.morningMinute.value)}",
                onTimeClick = { /* show TimePicker */ },
            )
            Spacer(Modifier.height(24.dp))
            NotificationToggleRow(
                label = "تذكير المساء",
                enabled = eveningEnabled,
                onEnabledChange = { viewModel.setEveningEnabled(it) },
                timeText = "${viewModel.eveningHour.value}:${String.format("%02d", viewModel.eveningMinute.value)}",
                onTimeClick = { /* show TimePicker */ },
            )
        }
    }
}
