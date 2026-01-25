package com.github.honqout.tvlauncher3.view.button

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.size.Precision
import com.github.honqout.tvlauncher3.bean.ActivityBean
import com.github.honqout.tvlauncher3.coil.model.ActivityIconModel
import com.github.honqout.tvlauncher3.utils.ApplicationUtils

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    item: ActivityBean,
    contentDefaultColor: Color = colorScheme.secondary,
    contentFocusedColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val imageRequest = remember(item.packageName) {
        ImageRequest.Builder(context)
            .data(ActivityIconModel(item.packageName, item.activityName))
            .precision(Precision.INEXACT)
            .allowHardware(true)
            .crossfade(false)
            .build()
    }

    RoundRectButton(
        modifier = modifier,
        icon = {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        if (item.iconType == ApplicationUtils.Companion.IconType.Banner) 0.dp
                        else 10.dp
                    ),
                model = imageRequest,
                contentDescription = item.label,
                contentScale = if (item.iconType == ApplicationUtils.Companion.IconType.Banner)
                    ContentScale.FillBounds else ContentScale.Fit
            )
        },
        label = item.label,
        backgroundColor = Color(item.color),
        contentDefaultColor = contentDefaultColor,
        contentFocusedColor = contentFocusedColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick
    )
}