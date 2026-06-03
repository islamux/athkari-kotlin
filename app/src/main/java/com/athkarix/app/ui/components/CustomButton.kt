package com.athkarix.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun CustomButton(
    icon: ImageVector = Icons.Default.ArrowBack,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColor.darkGold),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, fontFamily = FontFamily.SansSerif)
    }
}
