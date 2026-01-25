package com.github.honqout.tvlauncher3.view.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.bean.ActivityBean
import com.github.honqout.tvlauncher3.constants.ColorConstants

@Composable
fun AppShortcutButtonTv(
    modifier: Modifier = Modifier,
    item: ActivityBean?,
    onFocused: () -> Unit = {},
    onAddItem: () -> Unit = {},
    onStartApp: () -> Unit = {},
    onRemoveItem: () -> Unit = {},
    onReplaceItem: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopCenter),
        contentAlignment = Alignment.TopCenter
    ) {
        if (item == null) {
            RoundRectButtonTv(
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocused()
                        }
                    },
                drawableRes = R.drawable.baseline_add_24,
                label = stringResource(R.string.add_app),
                backgroundColor = ColorConstants.ButtonContainerDefault,
                contentDefaultColor = ColorConstants.ButtonContentDefault,
                contentFocusedColor = ColorConstants.ButtonContentFocused,
                onShortClick = onAddItem
            )
        } else {
            ActivityButtonTv(
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocused()
                        }
                    },
                item = item,
                contentDefaultColor = ColorConstants.ButtonContentDefault,
                contentFocusedColor = ColorConstants.ButtonContentFocused,
                onShortClick = onStartApp,
                onLongClick = {
                    expanded = true
                }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.remove))
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
                            .size(24.dp)
                    )
                },
                colors = MenuDefaults.itemColors()
            )

            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.replace))
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
                            .size(24.dp)
                    )
                },
                colors = MenuDefaults.itemColors()
            )
        }
    }
}