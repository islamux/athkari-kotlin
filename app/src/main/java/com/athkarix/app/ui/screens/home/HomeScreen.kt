package com.athkarix.app.ui.screens.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.athkarix.app.ui.components.common.ExitGuard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athkarix.app.ui.components.common.BackgroundImage
import com.athkarix.app.ui.components.common.CustomButton
import com.athkarix.app.ui.components.navigation.CustomDrawer
import com.athkarix.app.ui.theme.AppColor
import com.athkarix.app.util.ShareUtil
import com.athkarix.app.util.WhatsAppUtil
import kotlinx.coroutines.launch

/** Data for each category button on the home screen — label, route, and click handler. */
data class HomeButtonItem(
    val label: String,
    val route: String,
    val onClick: () -> Unit,
)

/** Main home screen: navigation drawer, background image, scrollable category buttons, exit dialog on back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    // — Observe navigation events from ViewModel —
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is HomeNavigationEvent.GoToRoute -> onNavigate(event.route)
            }
        }
    }

    // — 11 athkar category buttons —
    val buttons = remember {
        listOf(
          // viewModel::goToAssmaHussna = {viewModel.goToAssmaHussna()} *** viewModel.goToAssmaHussna WRONG becues  it exute before click btn the code 
            HomeButtonItem("أسماء الله الحسنى", "assma_hussna", viewModel::goToAssmaHussna),
            HomeButtonItem("الإستغفار", "estigfar", viewModel::goToEstigfar),
            HomeButtonItem("التسبيح", "tasbih", viewModel::goToTasbih),
            HomeButtonItem("الحمد", "hamd", viewModel::goToHamd),
            HomeButtonItem("الصلاة على النبي", "salat_ala_rasoul", viewModel::goToSalatAlaRasoul),
            HomeButtonItem("أذكار الصبـــاح", "athkar_sabah", viewModel::goToAthkarSabah),
            HomeButtonItem("أذكار المساء", "athkar_massa", viewModel::goToAthkarMassa),
            HomeButtonItem("الأذكار بعد الصلاة المفروضة", "athkar_after_salat", viewModel::goToAthkarAfterSalat),
            HomeButtonItem("الدعاء من السنــة", "duaa_sunnah", viewModel::goToDuaMenSunnah),
            HomeButtonItem("الدعاء من القراءن الكريم", "duaa_quran", viewModel::goToDuaMenQuran),
            HomeButtonItem("أذكار النوم", "athkar_before_bed", viewModel::goToAthkarBeforeBed),
        )
    }

    ExitGuard(
        showDialog = showExitDialog,
        onRequestExit = { showExitDialog = true },
        onConfirmExit = {
            showExitDialog = false
            (context as? Activity)?.finishAffinity()
        },
        onDismiss = { showExitDialog = false },
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                onNotificationSettings = {
                    scope.launch { drawerState.close() }
                    onNavigate("notification_settings")
                },
                onContactUs = {
                    scope.launch { drawerState.close() }
                    WhatsAppUtil.openWhatsApp(context)
                },
                onEmailUs = {
                    scope.launch { drawerState.close() }
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:fathi733@gmail.com")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) { }
                },
                onShare = {
                    scope.launch { drawerState.close() }
                    ShareUtil.shareApp(context)
                },
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Athkarix") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = AppColor.primaryGold,
                        actionIconContentColor = AppColor.amber,
                    ),
                    actions = {
                      // viewModel:: goToSearch = {viewModel.goToSearch()}
                        IconButton(onClick = { viewModel.goToSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "بحث")
                        }
                    },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                BackgroundImage()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item {
                            Text(
                                text = "إسحب للأعلى للمزيد",
                                color = AppColor.textSecondary,
                                fontSize = 14.sp,
                            )
                        }
                        items(buttons) { button ->
                            CustomButton(
                                icon = Icons.Default.Menu,
                                text = button.label,
                                onClick = button.onClick,
                            )
                        }
                    }
                }
            }
        }
    }
}
