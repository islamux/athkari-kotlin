package com.athkarix.app.ui.screens.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.athkarix.app.R
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.notification.NotificationToggleRow
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
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(drawableRes = R.drawable.bg_91k)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AthkarixTopAppBar(
                    title = stringResource(R.string.title_notification_settings),
                    onBack = onBack,
                )
            },
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                NotificationToggleRow(
                    label = stringResource(R.string.notification_settings_morning),
                    enabled = morningEnabled,
                    onEnabledChange = { viewModel.setMorningEnabled(it) },
                    timeText = "${viewModel.morningHour.value}:${String.format("%02d", viewModel.morningMinute.value)}",
                    onTimeClick = {
                        TimePickerDialog(context, { _, h, m ->
                            viewModel.setMorningTime(h, m)
                        }, viewModel.morningHour.value, viewModel.morningMinute.value, true).show()
                    },
                )
                Spacer(Modifier.height(24.dp))
                NotificationToggleRow(
                    label = stringResource(R.string.notification_settings_evening),
                    enabled = eveningEnabled,
                    onEnabledChange = { viewModel.setEveningEnabled(it) },
                    timeText = "${viewModel.eveningHour.value}:${String.format("%02d", viewModel.eveningMinute.value)}",
                    onTimeClick = {
                        TimePickerDialog(context, { _, h, m ->
                            viewModel.setEveningTime(h, m)
                        }, viewModel.eveningHour.value, viewModel.eveningMinute.value, true).show()
                    },
                )
            }
        }
    }
}
