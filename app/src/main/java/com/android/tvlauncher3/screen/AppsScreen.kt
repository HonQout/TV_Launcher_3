package com.android.tvlauncher3.screen

import android.content.pm.ResolveInfo
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.RoundRectButton
import com.android.tvlauncher3.view.dialog.AppActionDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppsScreen(
    viewModel: MainViewModel = viewModel()
) {
    val tag = "AppsScreen"
    val numColumns = 5
    val context = LocalContext.current
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
            Spacer(modifier = Modifier.height(topBarHeight.dp))

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .weight(weight = 1.0f)
                    .onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.Menu -> {
                                when (keyEvent.nativeKeyEvent.action) {
                                    KeyEvent.ACTION_DOWN -> {
                                        Log.i(tag, "Pressed key: Menu")
                                        false
                                    }

                                    KeyEvent.ACTION_UP -> {
                                        Log.i(tag, "Released key: Menu")
                                        coroutineScope.launch(Dispatchers.IO) {
                                            viewModel.setResolveInfo(viewModel.focusedItemResolveInfo.value)
                                            viewModel.setShowAppActionDialog(true)
                                        }
                                        true
                                    }

                                    else -> false
                                }
                            }

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
                itemsIndexed(
                    viewModel.activityBeanList
                ) { index, item ->
                    val focusRequester = remember { FocusRequester() }

                    RoundRectButton(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    Log.i(tag, "FocusedItemIndex: $index")
                                    viewModel.setFocusedItemIndex2(index)
                                    viewModel.setFocusedItemResolveInfo(item.resolveInfo)
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
                            viewModel.setPressedItemResolveInfo(item.resolveInfo)
                            viewModel.setResolveInfo(item.resolveInfo)
                            viewModel.setShowAppActionDialog(true)
                        }
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