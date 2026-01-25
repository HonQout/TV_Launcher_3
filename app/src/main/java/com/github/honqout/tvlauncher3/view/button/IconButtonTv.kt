package com.github.honqout.tvlauncher3.view.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import com.github.honqout.tvlauncher3.constants.ColorConstants

@Composable
fun IconButtonTv(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
    onShortClick: () -> Unit = {}
) {
    Button(
        onClick = onShortClick,
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .focusRequester(focusRequester)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShortClick() }
                )
            },
        enabled = true,
        scale = ButtonDefaults.scale(),
        shape = ButtonDefaults.shape(shape = CircleShape),
        colors = ButtonDefaults.colors(
            containerColor = ColorConstants.OnWallpaperContainer,
            contentColor = ColorConstants.ButtonContentDefault,
            focusedContainerColor = ColorConstants.ButtonContainerFocused,
            focusedContentColor = ColorConstants.ButtonContentFocused
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier
                .size(size = 20.dp)
        )
    }
}