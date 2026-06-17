package com.athkarix.app.ui.components.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.athkarix.app.R
import com.athkarix.app.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarixTopAppBar(
    title: String = "",
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val titleContent: @Composable () -> Unit = {
        Text(
            text = title,
            color = AppColor.primaryGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
    val navigationIconContent: @Composable () -> Unit = {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = AppColor.primaryGold,
                )
            }
        }
    }
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Black,
        titleContentColor = AppColor.primaryGold,
    )

    if (centered) {
        CenterAlignedTopAppBar(
            title = titleContent,
            navigationIcon = navigationIconContent,
            actions = actions,
            colors = colors,
            modifier = modifier,
        )
    } else {
        TopAppBar(
            title = titleContent,
            navigationIcon = navigationIconContent,
            actions = actions,
            colors = colors,
            modifier = modifier,
        )
    }
}
