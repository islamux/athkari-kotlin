package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.FontViewModel

/** +/- buttons to increase/decrease font size and toggle button for Amiri/Cairo. */
@Composable
fun FontControls(
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier
) {
    val currentFont by fontViewModel.selectedFont.collectAsState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = { fontViewModel.decreaseFontSize() }) {
            Icon(Icons.Default.Remove, contentDescription = "تصغير الخط")
        }
        TextButton(onClick = { fontViewModel.toggleFont() }) {
            Text(
                text = currentFont,
                color = AppColor.primaryGold,
            )
        }
        IconButton(onClick = { fontViewModel.increaseFontSize() }) {
            Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
        }
    }
}
