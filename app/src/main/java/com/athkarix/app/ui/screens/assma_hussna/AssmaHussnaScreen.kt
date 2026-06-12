package com.athkarix.app.ui.screens.assma_hussna

import androidx.compose.runtime.Composable
import com.athkarix.app.ui.screens.athkar.AthkarScreen
import com.athkarix.app.viewmodel.AssmaHussnaViewModel
import com.athkarix.app.viewmodel.FontViewModel

/** Wraps the generic AthkarScreen with the AssmaHussna ViewModel. */
@Composable
fun AssmaHussnaScreen(
    viewModel: AssmaHussnaViewModel,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit,
) {
    AthkarScreen(
        viewModel = viewModel,
        fontViewModel = fontViewModel,
        onBack = onBack,
        onShare = onShare,
    )
}
