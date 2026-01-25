package com.github.honqout.tvlauncher3.view.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.LauncherViewModel
import com.github.honqout.tvlauncher3.constants.ColorConstants
import com.github.honqout.tvlauncher3.utils.IntentUtils
import com.github.honqout.tvlauncher3.view.button.AppShortcutButtonTv
import com.github.honqout.tvlauncher3.view.dialog.AppListDialog

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel = viewModel()
) {
    val context = LocalContext.current
    val lazyGridState = rememberLazyGridState()
    val numFixedActivity = viewModel.numFixedActivity
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val showAppListDialog by viewModel.showAppListDialog.collectAsState()
    val fixedActivityBeanList by viewModel.fixedActivityListState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(topBarHeight.dp))

            Spacer(modifier = Modifier.weight(1f))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numFixedActivity),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ColorConstants.OnWallpaperContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .onFocusChanged { focusState ->
                        if (!focusState.hasFocus) {
                            viewModel.setFocusedItemIndex1(-1)
                        }
                    }
                    .focusable(false),
                state = lazyGridState,
                contentPadding = PaddingValues(all = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = true
            ) {
                itemsIndexed(
                    items = fixedActivityBeanList,
                    key = { index, item -> "key_${index}" }
                ) { index, item ->
                    AppShortcutButtonTv(
                        modifier = Modifier,
                        item = item,
                        onFocused = {
                            viewModel.setFocusedItemIndex1(index)
                        },
                        onAddItem = {
                            viewModel.setFocusedItemIndex1(index)
                            viewModel.setShowAppListScreen(true)
                        },
                        onStartApp = {
                            if (item != null) {
                                IntentUtils.handleLaunchActivityResult(
                                    context,
                                    IntentUtils.launchActivity(
                                        context,
                                        item.packageName,
                                        item.activityName,
                                        true
                                    )
                                )
                            }
                        },
                        onRemoveItem = {
                            viewModel.addItemToFixedActivityBeanList(
                                index = index,
                                item = null
                            )
                        },
                        onReplaceItem = {
                            viewModel.setFocusedItemIndex1(index)
                            viewModel.setShowAppListScreen(true)
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showAppListDialog,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            AppListDialog(
                viewModel = viewModel,
                onItemChosen = { _, activityBean ->
                    viewModel.addItemToFixedActivityBeanList(
                        index = null,
                        item = activityBean
                    )
                },
                onDismissRequest = {
                    viewModel.setShowAppListScreen(false)
                }
            )
        }
    }
}