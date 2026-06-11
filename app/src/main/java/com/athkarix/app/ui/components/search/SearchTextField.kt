package com.athkarix.app.ui.components.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
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
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "مسح", tint = AppColor.primaryGold)
                }
            }
        },
        modifier = modifier.fillMaxWidth().padding(8.dp),
        singleLine = true,
    )
}
