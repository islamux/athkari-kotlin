package com.athkarix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.athkarix.app.navigation.AthkarixNavGraph
import com.athkarix.app.ui.theme.AthkarixTheme

/** Single activity — sets the Compose content with the app theme and navigation graph. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AthkarixTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AthkarixNavGraph(navController = rememberNavController())
                }
            }
        }
    }
}
