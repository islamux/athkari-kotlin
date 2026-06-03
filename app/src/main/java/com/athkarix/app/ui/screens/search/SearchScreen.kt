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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onResultClick: (SearchViewModel.SearchResult) -> Unit,
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TextField(
            value = query,
            onValueChange = { viewModel.search(it) },
            placeholder = { Text("بحث في الأذكار...", color = AppColor.textSecondary) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = AppColor.primaryGold,
                unfocusedTextColor = AppColor.primaryGold,
                focusedContainerColor = AppColor.surface,
                unfocusedContainerColor = AppColor.surface,
                cursorColor = AppColor.primaryGold,
            ),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = AppColor.primaryGold) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.search("") }) {
                        Icon(Icons.Default.Clear, "مسح", tint = AppColor.primaryGold)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
        )
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
