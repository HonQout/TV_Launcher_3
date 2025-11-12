package com.android.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tvlauncher3.constants.NumberConstants

@Composable
fun AppActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    @StringRes contentDescriptionRes: Int = labelRes,
    onShortClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.1f else 1f,
        animationSpec = tween(durationMillis = NumberConstants.ANIM_DURATION_MS)
    )
    val containerColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.LightGray else Color.DarkGray,
        animationSpec = tween(durationMillis = NumberConstants.ANIM_DURATION_MS)
    )
    val contentColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.Black else Color.White,
        animationSpec = tween(durationMillis = NumberConstants.ANIM_DURATION_MS)
    )

    Button(
        onClick = onShortClick,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
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
                        when (keyEvent.nativeKeyEvent.action) {
                            KeyEvent.ACTION_UP -> {
                                onShortClick()
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            },
        enabled = true,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(),
        contentPadding = PaddingValues(start = 12.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .focusable(enabled = false),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = stringResource(contentDescriptionRes),
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer {
                        cameraDistance = 12f
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(contentColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(labelRes),
                modifier = Modifier,
                color = contentColor,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}