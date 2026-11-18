package com.github.honqout.tvlauncher3.ui.general.components.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun BlurMusk(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    bgColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(Color.Black.copy(alpha = 0.25f))
                .blur(
                    radius = 24.dp,
                    edgeTreatment = BlurredEdgeTreatment(shape = shape)
                )
        ) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = bgColor
            ) {}
        }

        content()
    }
}