package com.android.tvlauncher3.view.button

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.android.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.android.tvlauncher3.utils.DrawableUtils

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RoundedRectButtonImpl(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    backgroundColor: Color = colorScheme.primary,
    onShortClickCallback: () -> Unit = {},
    onLongClickCallback: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()
    val pressState = interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    val textColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {

    }

    Surface(
        modifier = modifier
            .size(width = 160.dp, height = 120.dp)
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Button,
                onClick = {
                    onShortClickCallback()
                },
                onLongClick = {
                    onLongClickCallback()
                },
            )
            .focusRequester(focusRequester)
            .focusable(
                enabled = true,
                interactionSource = interactionSource
            )
            .onFocusChanged {

            }
            .hoverable(
                enabled = true,
                interactionSource = interactionSource
            )
            .indication(
                interactionSource = interactionSource,
                indication = null
            )
            .onKeyEvent { keyEvent ->
                when (keyEvent.key) {
                    Key.Enter -> {
                        onShortClickCallback()
                        true
                    }

                    Key.Menu -> {
                        onLongClickCallback()
                        true
                    }

                    else -> {
                        false
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .focusProperties { canFocus = false }
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
fun RoundedRectButton(
    modifier: Modifier = Modifier,
    iconType: IconType,
    icon: Drawable,
    contentDescription: String,
    label: String,
    onShortClickCallback: () -> Unit = {},
    onLongClickCallback: () -> Unit = {}
) {
    val backgroundColor = Color(DrawableUtils.getDominantColor(icon))
    val iconWidth = when (iconType) {
        IconType.Icon -> 75.dp
        IconType.Banner -> 160.dp
    }
    val iconHeight = when (iconType) {
        IconType.Icon -> 75.dp
        IconType.Banner -> 90.dp
    }

    RoundedRectButtonImpl(
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
        onShortClickCallback = onShortClickCallback,
        onLongClickCallback = onLongClickCallback
    )
}

@Composable
fun RoundedRectButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    label: String,
    backgroundColor: Color = colorScheme.primary,
    onShortClickCallback: () -> Unit = {},
    onLongClickCallback: () -> Unit = {}
) {
    RoundedRectButtonImpl(
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
        onShortClickCallback = onShortClickCallback,
        onLongClickCallback = onLongClickCallback
    )
}