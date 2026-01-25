package com.github.honqout.tvlauncher3.view.dialog

import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.LauncherViewModel
import com.github.honqout.tvlauncher3.bean.ActivityBean
import com.github.honqout.tvlauncher3.constants.ColorConstants
import com.github.honqout.tvlauncher3.view.button.ActivityButtonTv
import kotlinx.coroutines.launch

@Composable
fun AppListDialog(
    viewModel: LauncherViewModel = viewModel(),
    onItemChosen: (index: Int, activityBean: ActivityBean) -> Unit = { _, _ -> },
    onDismissRequest: () -> Unit = {}
) {
    val tag = "AppListDialog"
    val numColumns = 5
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    var focusedItemIndex by remember { mutableIntStateOf(0) }

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

    BackHandler {
        Log.i(tag, "Pressed back button.")
        onDismissRequest()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_an_app),
                modifier = Modifier
                    .focusable(false),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .focusable(false)
                    //.focusRequester(focusRequester0)
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
                                        false
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
                                        false
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
                    viewModel.activityBeanList
                ) { index, item ->
                    val focusRequester = remember { FocusRequester() }

                    ActivityButtonTv(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    Log.i(tag, "FocusedItemIndex: $index")
                                    focusedItemIndex = index
                                }
                            },
                        item = item,
                        contentDefaultColor = ColorConstants.ButtonContentDefault,
                        contentFocusedColor = ColorConstants.ButtonContentFocused,
                        onShortClick = {
                            onItemChosen(index, item)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}