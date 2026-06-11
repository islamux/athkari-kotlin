package com.athkarix.app.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.athkarix.app.R

@Composable
fun BackgroundImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.bg_home),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
    )
}
