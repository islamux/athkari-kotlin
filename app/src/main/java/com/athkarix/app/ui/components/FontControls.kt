package com.athkarix.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.athkarix.app.viewmodel.FontViewModel

/** +/- buttons to increase / decrease the app-wide font size. */
@Composable
fun FontControls(
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = { fontViewModel.decreaseFontSize() }) {
            Icon(Icons.Default.Clear, contentDescription = "تصغير الخط")
        }
        IconButton(onClick = { fontViewModel.increaseFontSize() }) {
            Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
        }
    }
}
