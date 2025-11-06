package com.android.tvlauncher3.page

import android.content.pm.ResolveInfo
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.view.button.AppButton
import com.android.tvlauncher3.view.dialog.AppActionDialog
import kotlinx.coroutines.launch

@Composable
fun ApplicationsRoute(viewModel: MainViewModel, toDestination: () -> Unit) {
    ApplicationsPage(viewModel = viewModel, toDestination = toDestination)
}

@Composable
fun ApplicationsPage(
    viewModel: MainViewModel = viewModel(),
    toDestination: () -> Unit = {}
) {
    val tag = "ApplicationsPage"
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

    BackHandler {
        Log.i(tag, "Pressed back button.")
        toDestination()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .height(topBarHeight.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.apps),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = viewModel.activityBeanList.size.toString(),
                color = Color.White,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(numColumns),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent)
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
                                    horizontalScrollToFocusedItem()
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
                                    horizontalScrollToFocusedItem()
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