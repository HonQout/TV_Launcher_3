package com.android.tvlauncher3.view.button

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    @StringRes titleRes: Int,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .height(90.dp)
            .width(120.dp)
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
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f),
            contentColor = Color.White,
            focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            focusedContentColor = Color.Black
        ),
        tonalElevation = 12.dp,
        border = ButtonDefaults.border(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
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
            }
        }
    }
}

@Composable
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .height(90.dp)
            .width(120.dp)
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
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f),
            contentColor = Color.White,
            focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            focusedContentColor = Color.Black
        ),
        tonalElevation = 12.dp,
        border = ButtonDefaults.border(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
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
                    color = Color.White,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}