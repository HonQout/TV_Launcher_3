package com.android.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes contentDescriptionRes: Int = titleRes,
    onShortClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.1f else 1f,
        animationSpec = tween(durationMillis = 250)
    )
    val containerColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.Gray.copy(alpha = 0.5f)
        else Color.DarkGray.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 250)
    )
    val contentColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 250)
    )

    Button(
        onClick = onShortClick,
        modifier = modifier
            .height(90.dp)
            .width(120.dp)
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
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(contentDescriptionRes),
                modifier = Modifier
                    .size(size = 30.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(androidx.tv.material3.ButtonDefaults.IconSpacing))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(titleRes),
                    color = Color.White,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    @StringRes contentDescriptionRes: Int = titleRes,
    onShortClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusState = interactionSource.collectIsFocusedAsState()
    val hoverState = interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (focusState.value || hoverState.value) 1.1f else 1f,
        animationSpec = tween(durationMillis = 250)
    )
    val containerColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.Gray.copy(alpha = 0.5f)
        else Color.DarkGray.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 250)
    )
    val contentColor by animateColorAsState(
        targetValue = if (focusState.value || hoverState.value) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 250)
    )

    Button(
        onClick = onShortClick,
        modifier = modifier
            .height(90.dp)
            .width(120.dp)
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
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(contentDescriptionRes),
                modifier = Modifier
                    .size(size = 30.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(titleRes),
                    color = Color.White,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(descriptionRes),
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}