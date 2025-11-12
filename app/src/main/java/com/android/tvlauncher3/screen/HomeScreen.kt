package com.android.tvlauncher3.screen

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.constants.ColorConstants
import com.android.tvlauncher3.view.button.AppShortcutButton
import com.android.tvlauncher3.view.dialog.AppListDialog

@Composable
fun HomeScreen(
    viewModel: MainViewModel = viewModel()
) {
    val tag = "HomeScreen"
    val numColumns = 5
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val showAppListDialog by viewModel.showAppListDialog.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(topBarHeight.dp))

            Spacer(modifier = Modifier.weight(1f))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ColorConstants.ListBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.DirectionLeft -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: DirectionLeft")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: DirectionLeft")
                                        false
                                    }

                                    else -> false
                                }
                            }

                            Key.DirectionRight -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: DirectionRight")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: DirectionRight")
                                        false
                                    }

                                    else -> false
                                }
                            }

                            else -> false
                        }
                    },
                state = lazyGridState,
                contentPadding = PaddingValues(all = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = true
            ) {
                itemsIndexed(
                    viewModel.fixedActivityBeanList
                ) { index, item ->
                    AppShortcutButton(
                        modifier = Modifier,
                        viewModel = viewModel,
                        index = index,
                        item = item
                    )
                }
            }
        }

        if (showAppListDialog) {
            AppListDialog(
                viewModel = viewModel,
                backgroundColor = Color.Black,
                contentColor = Color.White,
                onItemChosen = { index, activityBean ->
                    viewModel.updateFixedActivityList(
                        index = null,
                        item = activityBean
                    )
                },
                onDismissRequest = {
                    viewModel.setShowAppListDialog(false)
                }
            )
        }
    }
}