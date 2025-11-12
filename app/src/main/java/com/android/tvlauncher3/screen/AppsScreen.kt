package com.android.tvlauncher3.screen

import android.content.pm.ResolveInfo
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.constants.ColorConstants
import com.android.tvlauncher3.view.button.AppButton
import com.android.tvlauncher3.view.dialog.AppActionDialog
import kotlinx.coroutines.launch

@Composable
fun AppsScreen(
    viewModel: MainViewModel = viewModel()
) {
    val tag = "AppsScreen"
    val numColumns = 5
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val showAppActionDialog by viewModel.showAppActionDialog.collectAsState()
    val focusedItemIndex by viewModel.focusedItemIndex2.collectAsState()
    val resolveInfo: ResolveInfo? by viewModel.resolveInfo.collectAsState()

    val centerFocusedItem = {
        if (focusedItemIndex in 0 until viewModel.activityBeanList.size) {
            val layoutInfo = lazyGridState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val firstVisibleItem = visibleItems.first()
                val focusedItemIndexOffset = focusedItemIndex - firstVisibleItem.index
                if (focusedItemIndexOffset in 0 until visibleItems.size) {
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2F
                    val focusedItem =
                        visibleItems[focusedItemIndexOffset]
                    val focusedItemStartOffset = focusedItem.offset.y
                    val focusedItemHeight = focusedItem.size.height
                    val focusedItemCenterOffset =
                        focusedItemStartOffset + focusedItemHeight / 2F
                    Log.i(tag, "Focused item center offset is $focusedItemCenterOffset")
                    val scrollOffset = focusedItemCenterOffset - viewportCenter
                    Log.i(tag, "Prepare to scroll. Offset = 0")
                    coroutineScope.launch {
                        lazyGridState.animateScrollBy(scrollOffset)
                    }
                } else {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(
                            focusedItemIndex
                        )
                    }
                }
            }
        }
    }

    val horizontalScrollToFocusedItem = {
        if (focusedItemIndex >= 0 && focusedItemIndex < viewModel.activityBeanList.size) {
            val layoutInfo = lazyGridState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val firstVisibleItem = visibleItems.first()
                val focusedItemIndexOffset =
                    focusedItemIndex - firstVisibleItem.index
                if (focusedItemIndexOffset < 0 || focusedItemIndexOffset >= visibleItems.size) {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(
                            focusedItemIndex
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height((topBarHeight + 10).dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ColorConstants.ListBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .weight(weight = 1.0f)
                    .onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: DirectionUp")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: DirectionUp")
                                        centerFocusedItem()
                                        true
                                    }

                                    else -> false
                                }
                            }

                            Key.DirectionDown -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: DirectionDown")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: DirectionDown")
                                        centerFocusedItem()
                                        true
                                    }

                                    else -> false
                                }
                            }

                            Key.DirectionLeft -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: DirectionLeft")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: DirectionLeft")
                                        horizontalScrollToFocusedItem()
                                        true
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
                                        horizontalScrollToFocusedItem()
                                        true
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
                itemsIndexed(viewModel.activityBeanList) { index, item ->
                    AppButton(
                        modifier = Modifier,
                        viewModel = viewModel,
                        index = index,
                        item = item
                    )
                }
            }
        }

        if (showAppActionDialog && resolveInfo != null) {
            AppActionDialog(
                resolveInfo = resolveInfo!!,
                onDismissRequest = {
                    viewModel.setShowAppActionDialog(false)
                },
            )
        }
    }
}