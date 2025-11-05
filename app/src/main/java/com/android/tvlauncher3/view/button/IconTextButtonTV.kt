package com.android.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults

@Composable
fun IconTextButtonTV(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes contentDescriptionRes: Int = titleRes,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
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

                    else -> {
                        false
                    }
                }
            },
        onLongClick = {},
        enabled = true,
        scale = ButtonDefaults.scale(),
        shape = ButtonDefaults.shape(shape = CircleShape),
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f),
            contentColor = Color.White,
            focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            focusedContentColor = Color.Black
        ),
        tonalElevation = 12.dp,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier
                .size(size = 20.dp),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(titleRes),
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}