package com.athkarix.app.ui.screens.assma_hussna

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.athkarix.app.ui.components.dua.AthkarTextSlider
import com.athkarix.app.ui.components.dua.FontControls
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.LoadingErrorContent
import com.athkarix.app.viewmodel.AssmaHussnaViewModel
import com.athkarix.app.viewmodel.FontViewModel

/** Displays the 99 Names with loading spinner, error+retry state, and the AthkarTextSlider. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssmaHussnaScreen(
    viewModel: AssmaHussnaViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    // — Three states: loading, error, or the slider —
    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            AthkarixTopAppBar(
                title = "أسماء الله الحسنى",
                onBack = onBack,
                actions = { FontControls(fontViewModel) },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LoadingErrorContent(
                isLoading = isLoading,
                hasError = hasError,
                errorMessage = errorMsg,
                onRetry = { viewModel.loadData() },
            ) {
                AthkarTextSlider(
                    viewModel = viewModel,
                    fontViewModel = fontViewModel,
                )
            }
        }
    }
}
