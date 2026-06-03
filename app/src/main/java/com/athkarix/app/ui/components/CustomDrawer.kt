package com.athkarix.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.theme.AppColor

@Composable
fun CustomDrawer(
    drawerState: DrawerState,
    onNotificationSettings: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Athkarix",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColor.primaryGold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Divider()
            Spacer(Modifier.height(8.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                label = { Text("إعدادات الإشعارات") },
                selected = false,
                onClick = onNotificationSettings,
                modifier = Modifier.fillMaxWidth()
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                label = { Text("مشاركة التطبيق") },
                selected = false,
                onClick = onShare,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
