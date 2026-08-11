package com.athkarix.app.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.R
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.dua.FontControls
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.util.ShareUtil
import com.athkarix.app.viewmodel.FontViewModel

/** Displays a single search result — the full dua text + footer with back navigation. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    item: AthkarItem,
    fontViewModel: FontViewModel,
    onBack: () -> Unit,
    onShare: (String) -> Unit = {},
    screenTitle: String = "",
) {
    val fontSize by fontViewModel.fontSize.collectAsState()
    val selectedFontName by fontViewModel.selectedFont.collectAsState()
    val fontFamily by remember {
        derivedStateOf {
            when (selectedFontName) {
                "NotoNaskh" -> FontFamily(Font(R.font.noto_naskh_arabic_regular, weight = FontWeight.Normal))
                else -> FontFamily(Font(R.font.amiri_regular, weight = FontWeight.Normal))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(drawableRes = R.drawable.bg_91k)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AthkarixTopAppBar(
                    title = screenTitle,
                    onBack = onBack,
                    actions = {
                        IconButton(onClick = { onShare(item.duaText ?: "") }) {
                            Icon(Icons.Default.Share, stringResource(R.string.cd_share), tint = AppColor.primaryGold)
                        }
                        FontControls(fontViewModel)
                    },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.duaText ?: "",
                            fontFamily = fontFamily,
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.5f).sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center,
                        )
                        if (!item.footer.isNullOrBlank()) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = item.footer,
                                fontFamily = FontFamily.Serif,
                                fontSize = (fontSize * 0.7f).sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF555555),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
}
