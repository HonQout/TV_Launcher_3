package com.github.honqout.tvlauncher3.ui.launcher.components.button

import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun IconFromDrawable(
    drawable: Drawable,
    contentDescription: String?
) {
    Image(
        painter = rememberDrawablePainter(drawable = drawable),
        contentDescription = contentDescription,
        modifier = Modifier
            .requiredSize(width = 75.dp, height = 75.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun IconFromResource(
    @DrawableRes drawableRes: Int,
    contentDescription: String?
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .requiredSize(width = 75.dp, height = 75.dp),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Composable
private fun RoundRectButtonImpl(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    containerColor: Color = colorScheme.background,
    defaultContentColor: Color = colorScheme.secondary,
    focusedContentColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() },
                    onLongPress = { onLongClick() }
                )
            }
            .onKeyEvent { keyEvent ->
                when (keyEvent.key) {
                    Key.Menu -> {
                        when (keyEvent.nativeKeyEvent.action) {
                            KeyEvent.ACTION_UP -> {
                                onMenuOpen()
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            },
        onClick = onShortClick,
        onLongClick = onLongClick,
        scale = ClickableSurfaceDefaults.scale(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = defaultContentColor,
            focusedContainerColor = Color.Transparent,
            focusedContentColor = focusedContentColor,
            pressedContainerColor = Color.Transparent,
            pressedContentColor = focusedContentColor
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(width = 160.dp, height = 90.dp),
                shape = RoundedCornerShape(16.dp),
                colors = SurfaceDefaults.colors(containerColor = containerColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = FONT_SIZE_M,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RoundRectButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    containerColor: Color = colorScheme.background,
    defaultContentColor: Color = colorScheme.secondary,
    focusedContentColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = onLongClick
) {
    RoundRectButtonImpl(
        modifier = modifier,
        icon = icon,
        label = label,
        containerColor = containerColor,
        defaultContentColor = defaultContentColor,
        focusedContentColor = focusedContentColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick,
        onMenuOpen = onMenuOpen
    )
}

@Composable
fun RoundRectButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    label: String,
    contentDescription: String = label,
    containerColor: Color = colorScheme.background,
    defaultContentColor: Color = colorScheme.secondary,
    focusedContentColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = onLongClick
) {
    RoundRectButtonImpl(
        modifier = modifier,
        icon = {
            IconFromResource(
                drawableRes = drawableRes,
                contentDescription = contentDescription
            )
        },
        label = label,
        containerColor = containerColor,
        defaultContentColor = defaultContentColor,
        focusedContentColor = focusedContentColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick,
        onMenuOpen = onMenuOpen
    )
}