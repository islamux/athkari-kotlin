package com.athkarix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.athkarix.app.navigation.AthkarixNavGraph
import com.athkarix.app.ui.theme.AthkarixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AthkarixTheme {
                AthkarixNavGraph(navController = rememberNavController())
            }
        }
    }
}
