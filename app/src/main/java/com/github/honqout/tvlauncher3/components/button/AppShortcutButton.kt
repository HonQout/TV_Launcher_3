package com.github.honqout.tvlauncher3.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
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
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentDefault
import com.github.honqout.tvlauncher3.ui.theme.ButtonContentFocused
import com.github.honqout.tvlauncher3.dto.ActivityDto

@Composable
fun AppShortcutButton(
    modifier: Modifier = Modifier,
    item: ActivityDto?,
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
            RoundRectButton(
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocused()
                        }
                    },
                drawableRes = R.drawable.baseline_add_24,
                label = stringResource(R.string.add_app),
                contentDefaultColor = ButtonContentDefault,
                contentFocusedColor = ButtonContentFocused,
                onShortClick = onAddItem
            )
        } else {
            AppButton(
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocused()
                        }
                    },
                item = item,
                contentDefaultColor = ButtonContentDefault,
                contentFocusedColor = ButtonContentFocused,
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