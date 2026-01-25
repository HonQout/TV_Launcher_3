package com.github.honqout.tvlauncher3.view.button

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.github.honqout.tvlauncher3.constants.UIConstants

@Composable
private fun RoundRectButtonTvImpl(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    icon: @Composable () -> Unit,
    label: String,
    backgroundColor: Color = colorScheme.background,
    contentDefaultColor: Color = colorScheme.secondary,
    contentFocusedColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .focusRequester(focusRequester)
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
            contentColor = contentDefaultColor,
            focusedContainerColor = Color.Transparent,
            focusedContentColor = contentFocusedColor
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(width = 160.dp, height = 90.dp),
                shape = RoundedCornerShape(16.dp),
                colors = SurfaceDefaults.colors(containerColor = backgroundColor)
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
                fontSize = UIConstants.FONT_SIZE_LARGE,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RoundRectButtonTv(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    backgroundColor: Color = colorScheme.background,
    contentDefaultColor: Color = colorScheme.secondary,
    contentFocusedColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = onLongClick
) {
    RoundRectButtonTvImpl(
        modifier = modifier,
        icon = icon,
        label = label,
        backgroundColor = backgroundColor,
        contentDefaultColor = contentDefaultColor,
        contentFocusedColor = contentFocusedColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick,
        onMenuOpen = onMenuOpen
    )
}

@Composable
fun RoundRectButtonTv(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    label: String,
    contentDescription: String = label,
    backgroundColor: Color = colorScheme.background,
    contentDefaultColor: Color = colorScheme.secondary,
    contentFocusedColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuOpen: () -> Unit = onLongClick
) {
    RoundRectButtonTvImpl(
        modifier = modifier,
        icon = {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier
                    .requiredSize(width = 75.dp, height = 75.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        label = label,
        backgroundColor = backgroundColor,
        contentDefaultColor = contentDefaultColor,
        contentFocusedColor = contentFocusedColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick,
        onMenuOpen = onMenuOpen
    )
}