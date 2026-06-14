package com.athkarix.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.athkarix.app.di.AppModule
import com.athkarix.app.ui.screens.athkar.AthkarScreen

import com.athkarix.app.ui.screens.home.HomeScreen
import com.athkarix.app.ui.screens.search.SearchScreen
import com.athkarix.app.ui.screens.search.SearchViewModel
import com.athkarix.app.ui.screens.settings.NotificationSettingsScreen
import com.athkarix.app.util.ShareUtil

/** String constants for every navigation route in the app — single source of truth. */
object Routes {
    const val HOME = "home"
    const val ATHKAR_SABAH = "athkar_sabah"
    const val ATHKAR_MASSA = "athkar_massa"
    const val ATHKAR_AFTER_SALAT = "athkar_after_salat"
    const val ATHKAR_BEFORE_BED = "athkar_before_bed"
    const val TASBIH = "tasbih"
    const val ESTIGFAR = "estigfar"
    const val HAMD = "hamd"
    const val SALAT_ALA_RASOUL = "salat_ala_rasoul"
    const val DUAA_QURAN = "duaa_quran"
    const val DUAA_SUNNAH = "duaa_sunnah"
    const val ASSMA_HUSSNA = "assma_hussna"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val SEARCH = "search"
    const val SEARCH_RESULT = "search_result/{categoryIndex}/{itemIndex}"
}

/** Top-level NavHost wiring every route constant to its screen composable + ViewModel. */
@Composable
fun AthkarixNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val fontVM = remember { AppModule.provideFontViewModel() }
    val floatingCounterVM = remember { AppModule.provideFloatingCounterViewModel() }

    
    fun back() { navController.popBackStack() }

    // — Route definitions —
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
    
      // Home screen
        composable(Routes.HOME) {
            val vm = remember { AppModule.provideHomeViewModel() }
            HomeScreen(
                viewModel = vm,
                onNavigate = { route -> navController.navigate(route) },
            )
        }

        // ATHKAR_SABAH screen
        composable(Routes.ATHKAR_SABAH) {
            val vm = remember { AppModule.provideAthkarSabahViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // ATHKAR_MASSA screen
        composable(Routes.ATHKAR_MASSA) {
            val vm = remember { AppModule.provideAthkarMassaViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // ATHKAR_AFTER_SALAT screen
        composable(Routes.ATHKAR_AFTER_SALAT) {
            val vm = remember { AppModule.provideAthkarAfterSalatViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // ATHKAR_BEFORE_BED screen
        composable(Routes.ATHKAR_BEFORE_BED) {
            val vm = remember { AppModule.provideAthkarBeforeBedViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // TASBIH screen
        composable(Routes.TASBIH) {
            val vm = remember { AppModule.provideTasbihViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // ESTIGFAR screen
        composable(Routes.ESTIGFAR) {
            val vm = remember { AppModule.provideEstigfarViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // HAMD screen
        composable(Routes.HAMD) {
            val vm = remember { AppModule.provideHamdViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }


        // SALAT_ALA_RASOUL screen
        composable(Routes.SALAT_ALA_RASOUL) {
            val vm = remember { AppModule.provideSalatAlaRasoulViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                floatingCounterVM = floatingCounterVM,
                showFloatingCounter = true,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // DUAA_QURAN screen
        composable(Routes.DUAA_QURAN) {
            val vm = remember { AppModule.provideDuaMenQuranViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // DUAA_SUNNAH screen
        composable(Routes.DUAA_SUNNAH) {
            val vm = remember { AppModule.provideDuaMenSunnahViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // ASSMA_HUSSNA screen
        composable(Routes.ASSMA_HUSSNA) {
            val vm = remember { AppModule.provideAssmaHussnaViewModel() }
            AthkarScreen(
                viewModel = vm,
                fontViewModel = fontVM,
                onBack = { back() },
                onShare = { text -> ShareUtil.shareText(context, text) },
            )
        }

        // NOTIFICATION_SETTINGS screen
        composable(Routes.NOTIFICATION_SETTINGS) {
            val vm = remember { AppModule.provideNotificationSettingsViewModel(context) }
            NotificationSettingsScreen(
                viewModel = vm,
                onBack = { back() },
            )
        }

        // SEARCH screen
        composable(Routes.SEARCH) {
            val searchVM = remember { SearchViewModel() }
            SearchScreen(
                viewModel = searchVM,
                onBack = { back() },
                onResultClick = { result ->
                    navController.navigate("${Routes.SEARCH_RESULT.replace("{categoryIndex}", result.categoryKey).replace("{itemIndex}", "${result.index}")}")
                },
            )
        }

        // SEARCH_RESULT screen
        composable(
            route = Routes.SEARCH_RESULT,
            arguments = listOf(
                navArgument("categoryIndex") { type = NavType.StringType },
                navArgument("itemIndex") { type = NavType.IntType },
            )
        ) { entry ->
            val categoryIndex = entry.arguments?.getString("categoryIndex") ?: ""
            val itemIndex = entry.arguments?.getInt("itemIndex") ?: 0
            PlaceholderScreenWithVM(
                name = "نتيجة البحث",
                vmInfo = "category: $categoryIndex, index: $itemIndex",
                onNavigate = { back() }
            )
        }
    }
}

/** Fallback screen for routes whose real UI is not yet implemented (e.g., search result display). */
@Composable
fun PlaceholderScreenWithVM(
    name: String,
    vmInfo: String,
    onNavigate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name, fontSize = 24.sp)
            Text(text = vmInfo, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Button(
                onClick = onNavigate,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back")
            }
        }
    }
}
