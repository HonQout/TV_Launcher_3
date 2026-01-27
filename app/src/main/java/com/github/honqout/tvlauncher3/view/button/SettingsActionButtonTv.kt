package com.github.honqout.tvlauncher3.view.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.constants.UIConstants

@Composable
fun SettingsActionButtonTv(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    @StringRes titleRes: Int,
    onShortClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .size(width = 120.dp, height = 90.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() }
                )
            },
        onClick = onShortClick,
        onLongClick = {},
        scale = ButtonDefaults.scale(),
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f),
            contentColor = Color.White,
            focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            focusedContentColor = Color.White,
            pressedContainerColor = Color.Gray.copy(alpha = 0.5f),
            pressedContentColor = Color.White
        ),
        tonalElevation = 12.dp,
        border = ButtonDefaults.border(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
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

            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(titleRes),
                    color = Color.White,
                    fontSize = UIConstants.FONT_SIZE_LARGE,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SettingsActionButtonTv(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    onShortClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .size(width = 120.dp, height = 90.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() }
                )
            },
        onClick = onShortClick,
        onLongClick = {},
        scale = ButtonDefaults.scale(),
        colors = ButtonDefaults.colors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f),
            contentColor = Color.White,
            focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            focusedContentColor = Color.White
        ),
        tonalElevation = 12.dp,
        border = ButtonDefaults.border(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
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
                    fontSize = UIConstants.FONT_SIZE_LARGE,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(descriptionRes),
                    color = Color.LightGray,
                    fontSize = UIConstants.FONT_SIZE_MEDIUM,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}