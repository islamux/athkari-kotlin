package com.athkarix.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.BaseAthkarViewModel
import com.athkarix.app.viewmodel.FontViewModel

/** Swipeable horizontal pager that renders each athkar page. RTL layout, scrollable content, click-to-advance. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AthkarTextSlider(
    viewModel: BaseAthkarViewModel,
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier
) {
    val pageIndex by viewModel.currentPageIndex.collectAsState()
    val fontSize by fontViewModel.fontSize.collectAsState()
    val fontFamily by fontViewModel.selectedFont.collectAsState()

    // — Sync pager with ViewModel page index —
    val pagerState = rememberPagerState(
        initialPage = pageIndex,
        pageCount = { viewModel.dataList.size }
    )

    LaunchedEffect(pageIndex) {
        if (pagerState.currentPage != pageIndex) {
            pagerState.animateScrollToPage(pageIndex)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        // — HorizontalPager with RTL: each page is one athkar item —
        HorizontalPager(
            state = pagerState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .clickable { viewModel.incrementPageController() }
                .padding(horizontal = 24.dp, vertical = 48.dp),
        ) { page ->
            val item = viewModel.dataList.getOrNull(page) ?: return@HorizontalPager
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = item.duaText ?: "",
                    fontFamily = if (fontFamily == "Amiri") FontFamily.Serif else FontFamily.SansSerif,
                    fontSize = fontSize.sp,
                    color = AppColor.primaryGold,
                    textAlign = TextAlign.Center,
                )
                if (!item.footer.isNullOrBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = item.footer,
                        fontFamily = FontFamily.Serif,
                        fontSize = (fontSize * 0.7f).sp,
                        color = AppColor.footer,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
