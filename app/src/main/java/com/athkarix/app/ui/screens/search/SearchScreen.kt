package com.athkarix.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import com.athkarix.app.ui.components.search.SearchTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.components.common.AthkarixTopAppBar
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.theme.AppColor

/** Live-search screen: text field filters across all athkar categories with diacritic-insensitive matching. */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onResultClick: (SearchViewModel.SearchResult) -> Unit,
    onBack: () -> Unit,
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Box(Modifier.fillMaxSize()) {
        BackgroundImage(scrimAlpha = 0.6f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            AthkarixTopAppBar(
                onBack = onBack,
            )
            SearchTextField(
                query = query,
                onQueryChange = { viewModel.search(it) },
            )
            // — Empty state vs results list —
            if (query.isNotEmpty() && results.isEmpty()) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "لا توجد نتائج",
                        color = AppColor.textSecondary,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            } else {
                LazyColumn {
                    items(results) { result ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    result.item.duaText ?: "",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = AppColor.primaryGold,
                                )
                            },
                            supportingContent = { Text(result.category, color = AppColor.textSecondary) },
                            modifier = Modifier.clickable { onResultClick(result) }
                        )
                    }
                }
            }
        }
    }
}
