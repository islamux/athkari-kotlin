package com.athkarix.app.ui.screens.athkar

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.athkarix.app.ui.components.AthkarTextSlider
import com.athkarix.app.ui.components.FloatingCounterFab
import com.athkarix.app.ui.components.FontControls
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.BaseAthkarViewModel
import com.athkarix.app.viewmodel.FloatingCounterViewModel
import com.athkarix.app.viewmodel.FontViewModel
import com.athkarix.app.viewmodel.ViewEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarScreen(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit,
    floatingCounterVM: FloatingCounterViewModel? = null,
    showFloatingCounter: Boolean = false,
) {
    val pageCounter by viewModel.currentPageCounter.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current

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

    LaunchedEffect(Unit) {
        viewModel.hapticTrigger.collect {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = AppColor.primaryGold)
                    }
                },
                title = {},
                actions = {
                    IconButton(onClick = {
                        onShare(viewModel.getShareText(viewModel.currentPageIndex.value))
                    }) {
                        Icon(Icons.Default.Share, "مشاركة", tint = AppColor.primaryGold)
                    }
                    FontControls(fontViewModel)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = AppColor.primaryGold
                )
            )
        },
        floatingActionButton = {
            if (showFloatingCounter && floatingCounterVM != null) {
                val count by floatingCounterVM.counter.collectAsState()
                FloatingCounterFab(counter = count, onClick = { floatingCounterVM.increment() })
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AthkarTextSlider(viewModel = viewModel, fontViewModel = fontViewModel)
        }
    }
}
