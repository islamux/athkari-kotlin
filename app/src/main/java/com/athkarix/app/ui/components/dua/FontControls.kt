package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.viewmodel.FontViewModel

private val fontDisplayNames = mapOf("Amiri" to "خط 1", "NotoNaskh" to "خط 2")

private val ControlShape = RoundedCornerShape(50)

/**
 * A compact segmented capsule for text adjustment: [ − ] | خط n ▾ | [ + ].
 * Groups size and typeface controls into one cohesive widget, using the same
 * Material add/remove icons as the original Flutter app, tinted in the app gold.
 */
@Composable
fun FontControls(
    fontViewModel: FontViewModel,
    modifier: Modifier = Modifier,
) {
    val currentFont by fontViewModel.selectedFont.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(ControlShape)
            .background(AppColor.primaryGold.copy(alpha = 0.10f))
            .border(0.5.dp, AppColor.primaryGold.copy(alpha = 0.30f), ControlShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FontControlIcon(
            icon = Icons.Default.Remove,
            description = "تصغير الخط",
            onClick = fontViewModel::decreaseFontSize,
        )
        VerticalDivider()
        Box {
            Row(
                modifier = Modifier
                    .clip(ControlShape)
                    .clickable { expanded = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = fontDisplayNames[currentFont] ?: currentFont,
                    color = AppColor.primaryGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = AppColor.primaryGold,
                    modifier = Modifier.size(16.dp),
                )
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
                                fontSize = 15.sp,
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
        VerticalDivider()
        FontControlIcon(
            icon = Icons.Default.Add,
            description = "تكبير الخط",
            onClick = fontViewModel::increaseFontSize,
        )
    }
}

@Composable
private fun FontControlIcon(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(ControlShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = AppColor.primaryGold,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun VerticalDivider() {
    Spacer(
        modifier = Modifier
            .height(18.dp)
            .width(0.5.dp)
            .background(AppColor.primaryGold.copy(alpha = 0.30f)),
    )
}
