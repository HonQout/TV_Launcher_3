package com.github.honqout.tvlauncher3.view.screen

import android.media.tv.TvContract
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.InputViewModel
import com.github.honqout.tvlauncher3.constants.ColorConstants
import com.github.honqout.tvlauncher3.databinding.TvViewLayoutBinding
import com.github.honqout.tvlauncher3.view.button.TvInputButton
import kotlinx.coroutines.launch

@Composable
fun InputScreen(
    viewModel: InputViewModel = viewModel()
) {
    val tag = "InputScreen"
    val numColumns = 1
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val focusedItemIndex by viewModel.focusedItemIndex.collectAsState()

    val centerFocusedItem = {
        if (focusedItemIndex in 0 until viewModel.tvInputList.size) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height((topBarHeight + 10).dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(numColumns),
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.2f)
                        .background(
                            color = ColorConstants.OnWallpaperContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .focusRestorer()
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

                                else -> false
                            }
                        },
                    state = lazyGridState,
                    contentPadding = PaddingValues(all = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = true
                ) {
                    itemsIndexed(viewModel.tvInputList) { index, item ->
                        TvInputButton(
                            modifier = Modifier,
                            index = index,
                            item = item,
                            onShortClick = {
                                viewModel.setFocusedItemIndex3(index)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                AndroidViewBinding(
                    factory = TvViewLayoutBinding::inflate,
                    modifier = Modifier,
                    update = {
                        val tvInputInfo = viewModel.getSelectedTvInputInfo()
                        if (tvInputInfo != null) {
                            val tvInputId = tvInputInfo.id
                            val tvInputUri =
                                TvContract.buildChannelUriForPassthroughInput(tvInputId)
                            tvView.tune(tvInputId, tvInputUri)
                        }
                    }
                )
            }
        }
    }
}