package com.android.tvlauncher3.page

import android.content.Context
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.RoundRectButton
import com.android.tvlauncher3.view.dialog.AppActionDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun ApplicationsRoute(context: Context, toDestination: () -> Unit, viewModel: MainViewModel) {
    ApplicationsPage(context = context, toDestination = toDestination, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsPage(
    context: Context,
    toDestination: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val tag = "ApplicationsPage"
    val numColumns = 5
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenHeightDp = LocalDensity.current.run {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val showAppActionDialog by viewModel.showAppActionDialog.collectAsState()
    val focusedItemIndex by viewModel.focusedItemIndex.collectAsState()
    val resolveInfo: ResolveInfo? by viewModel.selectedResolveInfo.collectAsState()
    val focusRequesters = remember(viewModel.activityBeanList.size) {
        List(viewModel.activityBeanList.size) { FocusRequester() }
    }
    val interactionSources = remember(viewModel.activityBeanList.size) {
        viewModel.activityBeanList.map { MutableInteractionSource() }
    }

    LaunchedEffect(focusedItemIndex) {
        snapshotFlow { focusedItemIndex }
            .distinctUntilChanged()
            .filter { it >= 0 && it < viewModel.activityBeanList.size }
            .collect { index ->
                val rowIndex = index / numColumns;

                val layoutInfo = lazyGridState.layoutInfo
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isNotEmpty()) {
                    val firstVisibleRow = visibleItems.first().index / numColumns
                    val lastVisibleRow = visibleItems.last().index / numColumns
                    val visibleRowCount = lastVisibleRow - firstVisibleRow + 1
                    val targetRow = max(0, rowIndex - visibleRowCount / 2)
                    lazyGridState.animateScrollToItem(
                        index = min(
                            targetRow * numColumns,
                            viewModel.activityBeanList.size - 1
                        )
                    )
                }
            }
    }

    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.title_page_apps),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = viewModel.activityBeanList.size.toString(),
                modifier = Modifier.padding(start = 10.dp),
                color = Color.White,
                fontSize = 30.sp,
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
                        Key.Menu -> {
                            when (keyEvent.nativeKeyEvent.action) {
                                KeyEvent.ACTION_DOWN -> {
                                    Log.i(tag, "Pressed key: Menu")
                                    false
                                }

                                KeyEvent.ACTION_UP -> {
                                    Log.i(tag, "Released key: Menu")
                                    coroutineScope.launch(Dispatchers.IO) {
                                        viewModel.setShowAppActionDialog(true)
                                    }
                                    false
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
                viewModel.activityBeanList
            ) { index, item ->
                val itemWidth = remember { mutableIntStateOf(160) }
                val itemHeight = remember { mutableIntStateOf(120) }
                val focusRequester = remember { FocusRequester() }

                RoundRectButton(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                Log.i(tag, "FocusedItemIndex: $index")
                                viewModel.setFocusedItemIndex(index)
                                viewModel.setSelectedResolveInfo(item.resolveInfo)
                            }
                        },
                    icon = item.getIcon(context),
                    iconType = item.iconType,
                    contentDescription = item.label,
                    label = item.label,
                    interactionSource = interactionSources[index],
                    onShortClickCallback = {
                        viewModel.setFocusedItemIndex(index)
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
                        viewModel.setShowAppActionDialog(true)
                    },
                    onSizeChanged = { intSize ->
                        Log.i(tag, "Width is ${intSize.width}, height is ${intSize.height}.")
                        itemWidth.intValue = intSize.width
                        itemHeight.intValue = intSize.height
                    }
                )
            }
        }
    }

    if (showAppActionDialog && resolveInfo != null) {
        AppActionDialog(
            context = context,
            resolveInfo = resolveInfo!!,
            onDismissRequest = {
                viewModel.setShowAppActionDialog(false)
            },
        )
    }
}