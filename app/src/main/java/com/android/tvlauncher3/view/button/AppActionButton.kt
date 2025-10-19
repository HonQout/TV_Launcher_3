package com.android.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Text

@Composable
fun AppActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    label: String,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
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
            focusedContentColor = Color.Black
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
            Image(
                painter = painterResource(iconRes),
                contentDescription = "app icon",
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        cameraDistance = 12f
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                modifier = Modifier,
                color = Color.White,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}