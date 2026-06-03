package com.athkarix.app.ui.screens.assma_hussna

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.components.AthkarTextSlider
import com.athkarix.app.ui.components.FontControls
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.AssmaHussnaViewModel
import com.athkarix.app.viewmodel.FontViewModel

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

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = AppColor.primaryGold)
                    }
                },
                title = { Text("أسماء الله الحسنى", color = AppColor.primaryGold) },
                actions = { FontControls(fontViewModel) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = AppColor.primaryGold
                )
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
