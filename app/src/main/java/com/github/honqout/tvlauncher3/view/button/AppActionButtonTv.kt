package com.github.honqout.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.constants.UIConstants

@Composable
fun AppActionButtonTv(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    @StringRes contentDescriptionRes: Int = labelRes,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() }
                )
            }
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
        onLongClick = {},
        enabled = true,
        scale = ButtonDefaults.scale(),
        glow = ButtonDefaults.glow(
            focusedGlow = Glow(
                elevationColor = Color.White,
                elevation = 2.dp
            )
        ),
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray,
            contentColor = Color.White,
            focusedContainerColor = Color.Gray,
            focusedContentColor = Color.Black,
            pressedContainerColor = Color.Gray,
            pressedContentColor = Color.Black
        ),
        tonalElevation = 12.dp,
        border = ButtonDefaults.border(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .focusProperties {
                    canFocus = false
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(contentDescriptionRes),
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer { cameraDistance = 12f }
            )

            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

            Text(
                text = stringResource(labelRes),
                modifier = Modifier,
                fontSize = UIConstants.FONT_SIZE_LARGE,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}