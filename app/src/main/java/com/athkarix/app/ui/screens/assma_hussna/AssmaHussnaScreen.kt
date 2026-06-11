package com.athkarix.app.ui.screens.assma_hussna

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.components.AthkarTextSlider
import com.athkarix.app.ui.components.FontControls
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.theme.AppColor
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
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColor.primaryGold
                    )
                }
                hasError -> {
                    Column(Modifier.align(Alignment.Center)) {
                        Text(errorMsg, color = AppColor.textSecondary)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("إعادة المحاولة")
                        }
                    }
                }
                else -> {
                    AthkarTextSlider(
                        viewModel = viewModel,
                        fontViewModel = fontViewModel,
                    )
                }
            }
        }
    }
}
