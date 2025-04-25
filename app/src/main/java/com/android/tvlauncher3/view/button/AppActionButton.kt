package com.android.tvlauncher3.view.button

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.android.tvlauncher3.utils.ColorUtils

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    label: String,
    onShortClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()
    val pressState = interactionSource.collectIsPressedAsState()

    var bgColor: Color = colorScheme.primary

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.15f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    val borderWidth by animateDpAsState(
        targetValue = if (focusState.value || hoverState.value) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val borderColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    Button(
        onClick = onShortClick,
        modifier = modifier
            .size(width = 70.dp, height = 80.dp)
            .scale(scale)
            .focusRequester(focusRequester)
            .focusable(
                enabled = true,
                interactionSource = interactionSource
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

                    else -> {
                        false
                    }
                }
            },
        enabled = true,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .focusProperties {
                    canFocus = false
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = "app icon",
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer {
                        cameraDistance = 12f
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                modifier = Modifier,
                color = ColorUtils.getAppropriateTextColor(bgColor),
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}