package com.athkarix.app.ui.screens.athkar

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.athkarix.app.R
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.dua.AthkarIndexDrawer
import com.athkarix.app.ui.components.dua.AthkarTextSlider
import com.athkarix.app.ui.components.dua.FontControls
import com.athkarix.app.ui.components.navigation.FloatingCounterFab
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.BaseAthkarViewModel
import com.athkarix.app.viewmodel.FloatingCounterViewModel
import com.athkarix.app.viewmodel.FontViewModel
import com.athkarix.app.viewmodel.ViewEvent
import kotlinx.coroutines.launch

/** Generic screen for any athkar category: top bar with back/share/font controls, text slider, optional floating counter, optional index drawer. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarScreen(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit,
    floatingCounterVM: FloatingCounterViewModel? = null,
    showFloatingCounter: Boolean = false,
    showIndex: Boolean = false,
    screenKey: String = "",
    screenTitle: String = "",
) {
    val pageCounter by viewModel.currentPageCounter.collectAsState()
    val pageIndex by viewModel.currentPageIndex.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    val indexDrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = indexDrawerState,
        gesturesEnabled = false,
        drawerContent = {
            if (showIndex) {
                AthkarIndexDrawer(
                    items = viewModel.dataList,
                    currentPage = pageIndex,
                    onItemClick = { index ->
                        viewModel.goToPage(index)
                        scope.launch { indexDrawerState.close() }
                    },
                )
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage(drawableRes = R.drawable.bg_91k)
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AthkarixTopAppBar(
                        title = screenTitle,
                        onBack = onBack,
                        actions = {
                            IconButton(onClick = {
                                onShare(viewModel.getShareText(viewModel.currentPageIndex.value))
                            }) {
                                Icon(Icons.Default.Share, stringResource(R.string.cd_share), tint = AppColor.primaryGold)
                            }
                            FontControls(fontViewModel)
                        },
                    )
                },
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding).padding(bottom = if (showFloatingCounter) 80.dp else 0.dp)) {
                    AthkarTextSlider(viewModel = viewModel, fontViewModel = fontViewModel)
                }
            }

            if (showIndex) {
                FloatingActionButton(
                    onClick = { scope.launch { indexDrawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(40.dp),
                    containerColor = Color.Black,
                    contentColor = AppColor.primaryGold,
                    shape = CircleShape,
                ) {
                    Icon(Icons.AutoMirrored.Filled.FormatListBulleted, stringResource(R.string.cd_index))
                }
            }

            if (showFloatingCounter && floatingCounterVM != null) {
                FloatingActionButton(
                    onClick = { viewModel.resetPageController() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(40.dp),
                    containerColor = Color.Black,
                    contentColor = AppColor.primaryGold,
                    shape = CircleShape,
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "إعادة تعيين")
                }
                FloatingCounterFab(
                    counter = pageCounter,
                    onClick = { viewModel.incrementPageController() },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                )
            }
        }
    }
}
