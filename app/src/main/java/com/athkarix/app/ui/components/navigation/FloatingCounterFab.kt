package com.athkarix.app.ui.components.navigation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor

/** Circular gold FAB showing a counter number — used for independent tasbih counting. */
@Composable
fun FloatingCounterFab(
    counter: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = AppColor.darkGold,
        contentColor = Color.White,
        shape = CircleShape,
    ) {
        Text(text = "$counter", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
