package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.FontViewModel

private val fontDisplayNames = mapOf("Amiri" to "خط 1", "Cairo" to "خط 2")

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
        IconButton(
            onClick = { fontViewModel.decreaseFontSize() },
            colors = IconButtonDefaults.iconButtonColors(contentColor = AppColor.primaryGold),
        ) {
            Icon(Icons.Default.Remove, contentDescription = "تصغير الخط")
        }
        Box {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = fontDisplayNames[currentFont] ?: currentFont,
                        color = AppColor.primaryGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AppColor.primaryGold,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                fontDisplayNames.forEach { (internal, display) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = display,
                                color = if (currentFont == internal) AppColor.primaryGold else AppColor.textSecondary,
                                fontSize = 16.sp,
                                fontWeight = if (currentFont == internal) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            fontViewModel.setFont(internal)
                            expanded = false
                        },
                    )
                }
            }
        }
        IconButton(
            onClick = { fontViewModel.increaseFontSize() },
            colors = IconButtonDefaults.iconButtonColors(contentColor = AppColor.primaryGold),
        ) {
            Icon(Icons.Default.Add, contentDescription = "تكبير الخط")
        }
    }
}
