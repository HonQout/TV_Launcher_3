package com.android.tvlauncher3.view.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TopBarActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    onShortClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()
    val pressState = interactionSource.collectIsPressedAsState()

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
        onClick = {
            onShortClick()
        },
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .scale(scale)
            .padding(1.dp)
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
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray.copy(alpha = 0.5f),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier
                .size(size = 24.dp),
            tint = Color.White
        )
    }
}