package com.athkarix.app.ui.screens.athkar

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.dua.AthkarTextSlider
import com.athkarix.app.ui.components.dua.FontControls
import com.athkarix.app.ui.components.navigation.FloatingCounterFab
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.BaseAthkarViewModel
import com.athkarix.app.viewmodel.FloatingCounterViewModel
import com.athkarix.app.viewmodel.FontViewModel
import com.athkarix.app.viewmodel.ViewEvent

/** Generic screen for any athkar category: top bar with back/share/font controls, text slider, optional floating counter. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarScreen(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit,
    floatingCounterVM: FloatingCounterViewModel? = null,
    showFloatingCounter: Boolean = false,
    screenKey: String = "",
) {
    val pageCounter by viewModel.currentPageCounter.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current

    // — Show completion snackbar when all items are done —
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ViewEvent.ShowCompletion -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    // — Haptic feedback on page advance —
    LaunchedEffect(Unit) {
        viewModel.hapticTrigger.collect {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(scrimAlpha = 0.6f)
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AthkarixTopAppBar(
                    onBack = onBack,
                    actions = {
                        IconButton(onClick = {
                            onShare(viewModel.getShareText(viewModel.currentPageIndex.value))
                        }) {
                            Icon(Icons.Default.Share, "مشاركة", tint = AppColor.primaryGold)
                        }
                        FontControls(fontViewModel)
                    },
                )
            },
            floatingActionButton = {
                if (showFloatingCounter && floatingCounterVM != null) {
                    val counters by floatingCounterVM.counters.collectAsState()
                    val count = counters[screenKey] ?: 0
                    FloatingCounterFab(counter = count, onClick = { floatingCounterVM.increment(screenKey) })
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                AthkarTextSlider(viewModel = viewModel, fontViewModel = fontViewModel)
            }
        }
    }
}
