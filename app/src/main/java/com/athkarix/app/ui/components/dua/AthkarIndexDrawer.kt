package com.athkarix.app.ui.components.dua

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.ui.theme.AppColor

/** Side drawer listing all athkar items by their opening words. Click any item to jump to that page. */
@Composable
fun AthkarIndexDrawer(
    items: List<AthkarItem>,
    currentPage: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = Color(0xFF1A1A1A),
        drawerContentColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "الفهرس",
                color = AppColor.primaryGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            items.forEachIndexed { index, item ->
                val label = item.duaText
                    ?.replace("\n", " ")
                    ?.take(40)
                    ?.trim()
                    ?: "صفحة ${index + 1}"
                val isCurrent = index == currentPage
                Text(
                    text = "${index + 1}. $label...",
                    color = if (isCurrent) AppColor.primaryGold else Color(0xFFCCCCCC),
                    fontSize = 18.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(index) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                )
                if (index < items.lastIndex) {
                    Divider(color = Color(0xFF333333))
                }
            }
        }
    }
}
