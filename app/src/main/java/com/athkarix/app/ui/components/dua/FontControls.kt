package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.FontViewModel

private val fontDisplayNames = mapOf("Amiri" to "أميري", "Cairo" to "القاهرة")

/** +/- buttons to increase/decrease font size and dropdown for font selection with Arabic labels. */
@Composable
fun FontControls(
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier
) {
    val currentFont by fontViewModel.selectedFont.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = { fontViewModel.decreaseFontSize() }) {
            Icon(Icons.Default.Remove, contentDescription = "تصغير الخط")
        }
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = "خط",
                    color = AppColor.primaryGold,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                fontDisplayNames.forEach { (internal, display) ->
                    DropdownMenuItem(
                        text = { Text(text = display, color = if (currentFont == internal) AppColor.primaryGold else AppColor.textSecondary) },
                        onClick = {
                            fontViewModel.setFont(internal)
                            expanded = false
                        },
                    )
                }
            }
        }
        IconButton(onClick = { fontViewModel.increaseFontSize() }) {
            Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
        }
    }
}
