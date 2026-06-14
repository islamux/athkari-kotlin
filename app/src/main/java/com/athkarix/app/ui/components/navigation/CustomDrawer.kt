package com.athkarix.app.ui.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Email
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

/** Navigation drawer with app branding, notification settings, contact us, and share. */
@Composable
fun CustomDrawer(
    onNotificationSettings: () -> Unit,
    onContactUs: () -> Unit,
    onEmailUs: () -> Unit,
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
                label = { Text("إعدادات التنبيهات") },
                selected = false,
                onClick = onNotificationSettings,
                modifier = Modifier.fillMaxWidth()
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                label = { Text("تواصل معنا") },
                selected = false,
                onClick = onContactUs,
                modifier = Modifier.fillMaxWidth()
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Email, contentDescription = null) },
                label = { Text("تواصل عبر البريد الإلكتروني") },
                selected = false,
                onClick = onEmailUs,
                modifier = Modifier.fillMaxWidth()
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                label = { Text("شارك التطبيق عبر وسائل التواصل") },
                selected = false,
                onClick = onShare,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
