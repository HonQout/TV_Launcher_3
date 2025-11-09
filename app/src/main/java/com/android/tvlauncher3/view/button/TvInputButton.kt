package com.android.tvlauncher3.view.button

import android.media.tv.TvInputInfo
import android.view.KeyEvent
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tvlauncher3.R

@Composable
fun TvInputButton(
    modifier: Modifier = Modifier,
    index: Int,
    item: TvInputInfo,
    onShortClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
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
            .wrapContentSize()
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(getTVInputIcon(item)),
                contentDescription = stringResource(R.string.input),
                modifier = Modifier
                    .size(24.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.id,
                color = Color.White,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@DrawableRes
fun getTVInputIcon(tvInputInfo: TvInputInfo): Int {
    return when (tvInputInfo.type) {
        TvInputInfo.TYPE_TUNER -> {
            R.drawable.baseline_television_classic_24
        }

        TvInputInfo.TYPE_COMPOSITE -> {
            R.drawable.baseline_video_input_component_24
        }

        TvInputInfo.TYPE_SVIDEO -> {
            R.drawable.baseline_video_input_svideo_24
        }

        TvInputInfo.TYPE_SCART -> {
            R.drawable.baseline_video_input_scart_24
        }

        TvInputInfo.TYPE_COMPONENT -> {
            R.drawable.baseline_video_input_component_24
        }

        TvInputInfo.TYPE_VGA -> {
            R.drawable.baseline_serial_port_24
        }

        TvInputInfo.TYPE_DVI -> {
            R.drawable.baseline_tv_24
        }

        TvInputInfo.TYPE_HDMI -> {
            R.drawable.baseline_hdmi_port_24
        }

        TvInputInfo.TYPE_DISPLAY_PORT -> {
            R.drawable.baseline_tv_24
        }

        else -> {
            R.drawable.baseline_tv_24
        }
    }
}