package com.android.tvlauncher3.view.button

import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.android.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.android.tvlauncher3.utils.DrawableUtils

@Composable
private fun RoundRectButtonImpl(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    backgroundColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.2f else 1f,
        animationSpec = tween(durationMillis = 250)
    )
    val textColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 250)
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .focusRequester(focusRequester)
            .focusable(
                enabled = true,
                interactionSource = interactionSource
            )
            .onKeyEvent { keyEvent ->
                when (keyEvent.key) {
                    Key.Enter -> {
                        when (keyEvent.nativeKeyEvent.action) {
                            KeyEvent.ACTION_UP -> {
                                onShortClick()
                                true
                            }

                            else -> false
                        }
                    }

                    Key.Menu -> {
                        when (keyEvent.nativeKeyEvent.action) {
                            KeyEvent.ACTION_UP -> {
                                onLongClick()
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Button,
                onClick = onShortClick,
                onLongClick = onLongClick,
            )
            .hoverable(
                enabled = true,
                interactionSource = interactionSource
            )
            .indication(
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .indication(
                    interactionSource = interactionSource,
                    indication = null
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(width = 160.dp, height = 90.dp),
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                modifier = Modifier,
                color = textColor,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RoundRectButton(
    modifier: Modifier = Modifier,
    iconType: IconType,
    icon: Drawable,
    label: String,
    contentDescription: String = label,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val backgroundColor =
        Color(DrawableUtils.getBackgroundColorFromAppIcon(icon, Color.White.toArgb()))
    val iconWidth = when (iconType) {
        IconType.Icon -> 75.dp
        IconType.Banner -> 160.dp
    }
    val iconHeight = when (iconType) {
        IconType.Icon -> 75.dp
        IconType.Banner -> 90.dp
    }

    RoundRectButtonImpl(
        modifier = modifier,
        icon = {
            Image(
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = contentDescription,
                modifier = Modifier
                    .requiredSize(width = iconWidth, height = iconHeight),
                contentScale = ContentScale.Fit
            )
        },
        label = label,
        backgroundColor = backgroundColor,
        onShortClick = onShortClick,
        onLongClick = onLongClick
    )
}

@Composable
fun RoundRectButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    label: String,
    contentDescription: String = label,
    backgroundColor: Color = colorScheme.primary,
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    RoundRectButtonImpl(
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
        onShortClick = onShortClick,
        onLongClick = onLongClick
    )
}