package com.athkarix.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = AppColor.primaryGold)
                    }
                },
                title = { Text("إعدادات التذكير", color = AppColor.primaryGold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = AppColor.primaryGold,
                )
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("تذكير الصباح", color = AppColor.textPrimary)
                Switch(
                    checked = morningEnabled,
                    onCheckedChange = { viewModel.setMorningEnabled(it) },
                )
            }
            if (morningEnabled) {
                TextButton(onClick = { /* show TimePicker */ }) {
                    Text(
                        "${viewModel.morningHour.value}:${String.format("%02d", viewModel.morningMinute.value)}",
                        color = AppColor.primaryGold,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("تذكير المساء", color = AppColor.textPrimary)
                Switch(
                    checked = eveningEnabled,
                    onCheckedChange = { viewModel.setEveningEnabled(it) },
                )
            }
            if (eveningEnabled) {
                TextButton(onClick = { /* show TimePicker */ }) {
                    Text(
                        "${viewModel.eveningHour.value}:${String.format("%02d", viewModel.eveningMinute.value)}",
                        color = AppColor.primaryGold,
                    )
                }
            }
        }
    }
}
