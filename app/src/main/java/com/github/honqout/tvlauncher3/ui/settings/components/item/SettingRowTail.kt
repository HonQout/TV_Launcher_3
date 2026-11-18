package com.github.honqout.tvlauncher3.ui.settings.components.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.honqout.tvlauncher3.R

@Composable
fun SettingRowTail() {
    Image(
        painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
        contentDescription = null,
        modifier = Modifier
            .requiredSize(width = 20.dp, height = 20.dp),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(Color.White)
    )
}