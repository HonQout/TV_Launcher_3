package com.github.honqout.tvlauncher3.ui.launcher.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.datastore.repository.IconRepository
import com.github.honqout.tvlauncher3.ui.launcher.components.button.AppShortcutButton
import com.github.honqout.tvlauncher3.ui.launcher.components.dialog.AppListDialog
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.LauncherViewModel
import com.github.honqout.tvlauncher3.ui.theme.OnWallpaperContainer
import com.github.honqout.tvlauncher3.ui.theme.PADDING_LIST_CONTENT_EDGE
import com.github.honqout.tvlauncher3.ui.theme.PADDING_SCREEN_EDGE
import com.github.honqout.tvlauncher3.ui.theme.SPACE_LIST_CONTENT_HORIZONTAL
import com.github.honqout.tvlauncher3.ui.theme.SPACE_LIST_CONTENT_VERTICAL
import com.github.honqout.tvlauncher3.utils.IntentUtils
import com.github.honqout.tvlauncher3.utils.IntentUtils.Companion.LaunchActivityResult

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    var showAppListDialog by rememberSaveable { mutableStateOf(false) }
    val fixedIconList by viewModel.fixedIconList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PADDING_SCREEN_EDGE)
        ) {
            Spacer(modifier = Modifier.height(topBarHeight.dp))

            Spacer(modifier = Modifier.weight(1f))

            LazyVerticalGrid(
                columns = GridCells.Fixed(IconRepository.NUM_FIXED_ACTIVITY),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = OnWallpaperContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .onFocusChanged { focusState ->
                        if (!focusState.hasFocus) {
                            viewModel.setFocusedFixedIconIndex(-1)
                        }
                    }
                    .focusRestorer(),
                state = lazyGridState,
                contentPadding = PaddingValues(PADDING_LIST_CONTENT_EDGE),
                horizontalArrangement = Arrangement.spacedBy(SPACE_LIST_CONTENT_HORIZONTAL),
                verticalArrangement = Arrangement.spacedBy(SPACE_LIST_CONTENT_VERTICAL),
                userScrollEnabled = true
            ) {
                itemsIndexed(
                    items = fixedIconList,
                    key = { index, item -> "key_${index}_${item?.packageName ?: "null"}_${item?.activityName ?: "null"}" }
                ) { index, item ->
                    AppShortcutButton(
                        activityModel = item,
                        onFocused = {
                            viewModel.setFocusedFixedIconIndex(index)
                        },
                        onAddItem = {
                            viewModel.setFocusedFixedIconIndex(index)
                            showAppListDialog = true
                        },
                        onStartApp = {
                            item?.let { item ->
                                val result = IntentUtils.launchActivity(
                                    context,
                                    item.packageName,
                                    item.activityName,
                                    true
                                )
                                when (result) {
                                    LaunchActivityResult.SUCCESS -> {

                                    }

                                    LaunchActivityResult.NOT_FOUND -> {
                                        Toast.makeText(
                                            context,
                                            R.string.cannot_find_activity,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    LaunchActivityResult.NOT_EXPORTED -> {
                                        Toast.makeText(
                                            context,
                                            R.string.cannot_access_unexported_activity,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    LaunchActivityResult.REQUIRE_EXTRA_PERMISSION -> {
                                        Toast.makeText(
                                            context,
                                            R.string.activity_requires_extra_permission,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        onRemoveItem = {
                            viewModel.setIcon(
                                position = index,
                                item = null
                            )
                        },
                        onReplaceItem = {
                            viewModel.setFocusedFixedIconIndex(index)
                            showAppListDialog = true
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
                onItemChosen = { _, activityModel ->
                    viewModel.setIcon(
                        position = null,
                        item = activityModel
                    )
                },
                onDismissRequest = {
                    showAppListDialog = false
                }
            )
        }
    }
}