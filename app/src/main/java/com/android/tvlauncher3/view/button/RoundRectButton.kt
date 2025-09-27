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
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.android.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.android.tvlauncher3.utils.DrawableUtils

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RoundRectButtonImpl(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    backgroundColor: Color = colorScheme.primary,
    interactionSource: InteractionSource = remember { MutableInteractionSource() },
    onShortClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onSizeChanged: (intSize: IntSize) -> Unit = {}
) {
    val interactionSource = interactionSource as MutableInteractionSource
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()
    val pressState = interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.2f else 1f,
        animationSpec = tween(durationMillis = 250)
    )
    val textColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 250)
    )
    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
    }

    Surface(
        modifier = modifier
            .onSizeChanged { intSize ->
                size = intSize
                onSizeChanged(intSize)
            }
            .scale(scale)
            .focusable(
                enabled = true,
                interactionSource = interactionSource
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Button,
                onClick = {
                    onShortClick()
                },
                onLongClick = {
                    onLongClick()
                },
            )
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
                        onShortClick()
                        true
                    }

                    Key.Menu -> {
                        onLongClick()
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
    contentDescription: String,
    label: String,
    interactionSource: InteractionSource = remember { MutableInteractionSource() },
    onShortClickCallback: () -> Unit = {},
    onLongClickCallback: () -> Unit = {},
    onSizeChanged: (IntSize) -> Unit = {}
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
        interactionSource = interactionSource,
        backgroundColor = backgroundColor,
        onShortClick = onShortClickCallback,
        onLongClick = onLongClickCallback,
        onSizeChanged = onSizeChanged
    )
}

@Composable
fun RoundRectButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    label: String,
    interactionSource: InteractionSource = remember { MutableInteractionSource() },
    backgroundColor: Color = colorScheme.primary,
    onShortClickCallback: () -> Unit = {},
    onLongClickCallback: () -> Unit = {},
    onSizeChanged: (IntSize) -> Unit = {}
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
        interactionSource = interactionSource,
        backgroundColor = backgroundColor,
        onShortClick = onShortClickCallback,
        onLongClick = onLongClickCallback,
        onSizeChanged = onSizeChanged
    )
}