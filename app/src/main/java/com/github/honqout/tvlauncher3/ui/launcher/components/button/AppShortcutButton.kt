package com.github.honqout.tvlauncher3.ui.launcher.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.activity.ActivityModel
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentDefault
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentFocused
import com.github.honqout.tvlauncher3.ui.theme.DropdownMenuContainer

@Composable
fun AppShortcutButton(
    modifier: Modifier = Modifier,
    activityModel: ActivityModel?,
    onFocused: () -> Unit = {},
    onAddItem: () -> Unit = {},
    onStartApp: () -> Unit = {},
    onRemoveItem: () -> Unit = {},
    onReplaceItem: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var buttonWidth by remember { mutableIntStateOf(0) }
    var menuWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    ) {
        ActivityButton(
            modifier = Modifier
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocused()
                    }
                }
                .onGloballyPositioned { coordinates ->
                    buttonWidth = coordinates.size.width
                },
            activityModel = activityModel,
            defaultIconRes = R.drawable.baseline_add_24,
            defaultContentColor = ButtonContentDefault,
            focusedContentColor = ButtonContentFocused,
            onShortClick = if (activityModel == null) onAddItem else onStartApp,
            onLongClick = {
                if (activityModel != null) {
                    expanded = true
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                menuWidth = coordinates.size.width
            },
            offset = DpOffset(
                x = with(LocalDensity.current) {
                    ((buttonWidth - menuWidth) / 2).toDp()
                },
                y = 0.dp
            ),
            containerColor = DropdownMenuContainer
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.remove),
                        color = Color.White
                    )
                },
                onClick = {
                    onRemoveItem()
                    expanded = false
                },
                modifier = Modifier,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = stringResource(R.string.remove),
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.White
                    )
                },
                colors = MenuDefaults.itemColors().copy(
                    textColor = Color.White,
                    leadingIconColor = Color.White
                )
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.replace),
                        color = Color.White
                    )
                },
                onClick = {
                    onReplaceItem()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = stringResource(R.string.replace),
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.White
                    )
                },
                colors = MenuDefaults.itemColors().copy(
                    textColor = Color.White,
                    leadingIconColor = Color.White
                )
            )
        }
    }
}