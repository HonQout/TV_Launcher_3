package com.android.tvlauncher3.view.button

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.bean.ActivityBean
import com.android.tvlauncher3.utils.IntentUtils

@Composable
fun AppShortcutButton(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    index: Int,
    item: ActivityBean?
) {
    val tag = "AppShortcutButton"
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    ) {
        if (item == null) {
            RoundRectButton(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            Log.i(tag, "FocusedItemIndex: $index")
                            viewModel.setFocusedItemIndex1(index)
                        }
                    },
                drawableRes = R.drawable.baseline_add_24,
                label = stringResource(R.string.add_app),
                onShortClickCallback = {
                    viewModel.setFocusedItemIndex1(index)
                    viewModel.setShowAppListDialog(true)
                }
            )
        } else {
            RoundRectButton(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            Log.i(tag, "FocusedItemIndex: $index")
                            viewModel.setFocusedItemIndex1(index)
                        }
                    },
                icon = item.getIcon(context),
                iconType = item.iconType,
                label = item.label,
                onShortClickCallback = {
                    val packageName = item.packageName
                    val result: Boolean =
                        IntentUtils.launchApp(context, packageName, true)
                    if (!result) {
                        Toast.makeText(
                            context,
                            ContextCompat.getString(
                                context,
                                R.string.hint_cannot_launch_app
                            ),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                onLongClickCallback = {
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
                    viewModel.updateFixedActivityList(
                        index = index,
                        item = null
                    )
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = stringResource(R.string.remove),
                        modifier = Modifier
                            .size(32.dp)
                    )
                },
                colors = MenuDefaults.itemColors()
            )
        }
    }
}