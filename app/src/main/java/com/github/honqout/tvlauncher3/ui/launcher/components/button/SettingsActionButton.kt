package com.github.honqout.tvlauncher3.ui.launcher.components.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.github.honqout.tvlauncher3.ui.theme.ButtonContainerDefault
import com.github.honqout.tvlauncher3.ui.theme.ButtonContainerFocused
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentDefault
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentFocused
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S
import com.github.honqout.tvlauncher3.ui.theme.SETTINGS_ACTION_BUTTON_HEIGHT
import com.github.honqout.tvlauncher3.ui.theme.SETTINGS_ACTION_BUTTON_WIDTH
import com.github.honqout.tvlauncher3.ui.theme.SPACE_ICON_TEXT_BUTTON

@Composable
fun SettingsIconFromResource(
    @DrawableRes drawableRes: Int,
    contentDescription: String?
) {
    Icon(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(size = 30.dp),
        tint = Color.White
    )
}

@Composable
private fun SettingsActionButtonImpl(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String?,
    title: String,
    description: String?,
    onShortClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .wrapContentSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() }
                )
            },
        onClick = onShortClick,
        onLongClick = {},
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.colors(
            containerColor = ButtonContainerDefault,
            contentColor = ButtonContentDefault,
            focusedContainerColor = ButtonContainerFocused,
            focusedContentColor = ButtonContentFocused
        ),
        tonalElevation = 12.dp,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Row(
            modifier = Modifier
                .size(
                    width = SETTINGS_ACTION_BUTTON_WIDTH,
                    height = SETTINGS_ACTION_BUTTON_HEIGHT
                ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIconFromResource(
                drawableRes = iconRes,
                contentDescription = contentDescription
            )

            Spacer(modifier = Modifier.width(SPACE_ICON_TEXT_BUTTON))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = FONT_SIZE_M,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                if (!description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = description,
                        color = Color.LightGray,
                        fontSize = FONT_SIZE_S,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null,
    title: String,
    description: String? = null,
    onShortClick: () -> Unit = {}
) {
    SettingsActionButtonImpl(
        modifier = modifier,
        iconRes = iconRes,
        contentDescription = contentDescription,
        title = title,
        description = description,
        onShortClick = onShortClick
    )
}

@Composable
fun SettingsActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int? = null,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int? = null,
    onShortClick: () -> Unit = {}
) {
    SettingsActionButtonImpl(
        modifier = modifier,
        iconRes = iconRes,
        contentDescription = if (contentDescriptionRes != null) stringResource(contentDescriptionRes) else null,
        title = stringResource(titleRes),
        description = if (descriptionRes != null) stringResource(descriptionRes) else null,
        onShortClick = onShortClick
    )
}