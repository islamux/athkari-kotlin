package com.athkarix.app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Detect tablet screens and scale font sizes accordingly for a good reading experience. */
object FontScaleUtil {

    @Composable
    fun isTablet(): Boolean = LocalConfiguration.current.screenWidthDp >= 600

    @Composable
    fun scaledFontSize(baseSize: Float): Float =
        if (isTablet()) baseSize * 1.5f else baseSize

    @Composable
    fun scaledSize(base: Float): Float =
        if (isTablet()) base * 1.5f else base
}
