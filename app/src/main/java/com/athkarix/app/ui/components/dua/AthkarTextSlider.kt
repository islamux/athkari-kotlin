package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.athkarix.app.R
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
    val selectedFontName by fontViewModel.selectedFont.collectAsState()
    val fontFamily by remember {
        derivedStateOf {
            when (selectedFontName) {
                "Cairo" -> FontFamily(Font(R.font.cairo_regular, weight = FontWeight.Normal))
                else -> FontFamily(Font(R.font.amiri_regular, weight = FontWeight.Normal))
            }
        }
    }

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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Column(modifier = modifier.fillMaxSize()) {
        // — HorizontalPager (RTL via CompositionLocal) —
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
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
                        fontFamily = fontFamily,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.5f).sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                    if (!item.footer.isNullOrBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = item.footer,
                            fontFamily = FontFamily.Serif,
                            fontSize = (fontSize * 0.7f).sp,
                            lineHeight = (fontSize * 0.7f * 1.5f).sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        // — Page slider / progress —
        val totalPages = viewModel.dataList.size
        if (totalPages > 1) {
            var isDragging by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isDragging) {
                    Text(
                        text = "${pageIndex + 1}",
                        modifier = Modifier
                            .background(AppColor.darkGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "${pageIndex + 1}",
                        color = AppColor.darkGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Spacer(Modifier.width(8.dp))
                    Slider(
                        value = pageIndex.toFloat(),
                        onValueChange = {
                            isDragging = true
                            viewModel.goToPage(it.toInt())
                        },
                        onValueChangeFinished = { isDragging = false },
                        valueRange = 0f..(totalPages - 1).toFloat(),
                        steps = totalPages - 2,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = AppColor.primaryGold,
                            activeTrackColor = AppColor.darkGold,
                            inactiveTrackColor = Color(0xFF555555),
                        ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${totalPages}",
                        color = AppColor.darkGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
    }
}
