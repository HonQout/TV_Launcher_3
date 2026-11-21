package com.github.honqout.tvlauncher3.ui.launcher.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Icon
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.constants.NumberConstants
import com.github.honqout.tvlauncher3.ui.launcher.components.button.IconButton
import com.github.honqout.tvlauncher3.ui.launcher.components.controlcenter.ControlCenter
import com.github.honqout.tvlauncher3.ui.launcher.screen.AppsScreen
import com.github.honqout.tvlauncher3.ui.launcher.screen.HomeScreen
import com.github.honqout.tvlauncher3.ui.launcher.screen.InputScreen
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.InputViewModel
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.LauncherViewModel
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.TimeViewModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S
import com.github.honqout.tvlauncher3.ui.theme.OnWallpaperContainer
import com.github.honqout.tvlauncher3.ui.theme.PADDING_SCREEN_EDGE
import com.github.honqout.tvlauncher3.ui.theme.PADDING_TOP_BAR
import com.github.honqout.tvlauncher3.ui.theme.TVLauncher3Theme
import com.github.honqout.tvlauncher3.ui.theme.TabContainerColorActive
import com.github.honqout.tvlauncher3.ui.theme.TabContainerColorInactive
import com.github.honqout.tvlauncher3.ui.theme.TabContentColorActive
import com.github.honqout.tvlauncher3.ui.theme.TabContentColorHovered
import com.github.honqout.tvlauncher3.ui.theme.TabContentColorInactive
import com.github.honqout.tvlauncher3.utils.DisplayUtils
import com.github.honqout.tvlauncher3.utils.UIUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val timeViewModel: TimeViewModel by viewModels()
    private val launcherViewModel: LauncherViewModel by viewModels()
    private val inputViewModel: InputViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide status bar and navigation bar
        UIUtils.handleSystemBarsVisibility(window, false)

        setContent {
            DisposableEffect(Unit) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    ) {
                        true
                    },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    ) {
                        true
                    }
                )
                onDispose {}
            }

            BackHandler {
                Log.i(TAG, "Pressed back button.")
            }

            TVLauncher3Theme {
                val configuration = LocalConfiguration.current

                val tabs = launcherViewModel.tabs
                val topBarHeight by launcherViewModel.topBarHeight.collectAsStateWithLifecycle()
                val selectedTabIndex by launcherViewModel.selectedTabIndex.collectAsStateWithLifecycle()
                var showControlCenter by rememberSaveable { mutableStateOf(false) }
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    delay(100.milliseconds)
                    // Handle config changes
                    launcherViewModel.onConfigChanged(configuration)
                    inputViewModel.onConfigChanged(configuration)
                    // Request focus
                    focusRequester.requestFocus()
                    Log.i(TAG, "Focused TabRow.")
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .background(Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(PADDING_SCREEN_EDGE)
                            .onSizeChanged { intSize ->
                                val heightDp = DisplayUtils.pixelToDp(baseContext, intSize.height)
                                launcherViewModel.setTopBarHeight(heightDp)
                                inputViewModel.setTopBarHeight(heightDp)
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(OnWallpaperContainer)
                                .focusRestorer()
                                .focusRequester(focusRequester),
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions, doesTabRowHaveFocus ->
                                TabRowDefaults.PillIndicator(
                                    currentTabPosition = tabPositions[selectedTabIndex],
                                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                                    modifier = Modifier,
                                    activeColor = TabContainerColorActive,
                                    inactiveColor = TabContainerColorInactive
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                val interactionSource = remember { MutableInteractionSource() }
                                val focusState = interactionSource.collectIsFocusedAsState()
                                val hoverState = interactionSource.collectIsHoveredAsState()
                                val bgColor by animateColorAsState(
                                    targetValue = if (focusState.value || hoverState.value)
                                        TabContainerColorActive
                                    else Color.Transparent,
                                    animationSpec = tween(durationMillis = NumberConstants.ANIM_DURATION_MS)
                                )
                                val contentColor by animateColorAsState(
                                    targetValue = if (selectedTabIndex == index)
                                        TabContentColorActive
                                    else if (focusState.value || hoverState.value)
                                        TabContentColorHovered
                                    else TabContentColorInactive
                                )

                                key(index) {
                                    Tab(
                                        selected = selectedTabIndex == index,
                                        onFocus = {
                                            Log.i(TAG, "Focused tab #$index.")
                                        },
                                        modifier = Modifier
                                            .background(color = bgColor, shape = CircleShape)
                                            .combinedClickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                enabled = true,
                                                role = Role.Tab,
                                                onClick = {
                                                    Log.i(TAG, "Clicked tab #$index.")
                                                    launcherViewModel.setSelectedTabIndex(index)
                                                }
                                            ),
                                        onClick = {
                                            Log.i(TAG, "Clicked tab #$index by remote controller.")
                                            launcherViewModel.setSelectedTabIndex(index)
                                        },
                                        colors = TabDefaults.pillIndicatorTabColors(
                                            contentColor = Color.White.copy(alpha = 0.7f),
                                            inactiveContentColor = Color.White.copy(alpha = 0.5f),
                                            selectedContentColor = TabContentColorActive,
                                            focusedContentColor = TabContentColorHovered,
                                            focusedSelectedContentColor = TabContentColorActive,
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painterResource(tab.first),
                                                contentDescription = stringResource(tab.second),
                                                tint = contentColor
                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = stringResource(tab.second),
                                                color = contentColor,
                                                fontSize = FONT_SIZE_S
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescription = stringResource(R.string.settings),
                            onShortClick = {
                                showControlCenter = true
                            }
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        AndroidView(
                            factory = { context ->
                                TextClock(context).apply {
                                    format12Hour = "hh:mm:ss"
                                    format24Hour = "HH:mm:ss"
                                    textSize = 20F
                                    setEnabled(false)
                                    setTextColor(android.graphics.Color.WHITE)
                                }
                            },
                            modifier = Modifier
                                .background(
                                    color = OnWallpaperContainer,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(10.dp)
                                .focusable(enabled = false)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Transparent)
                    ) {
                        when (selectedTabIndex) {
                            0 -> HomeScreen(viewModel = launcherViewModel)
                            1 -> AppsScreen(viewModel = launcherViewModel)
                            2 -> InputScreen(viewModel = inputViewModel)
                            else -> launcherViewModel.setSelectedTabIndex(0)
                        }
                    }

                    if (showControlCenter) {
                        val horizontalSpacingPx =
                            with(LocalDensity.current) { PADDING_SCREEN_EDGE.roundToPx() }
                        val verticalSpacingPx =
                            with(LocalDensity.current) { (PADDING_SCREEN_EDGE + topBarHeight.dp + PADDING_TOP_BAR).roundToPx() }

                        ControlCenter(
                            alignment = Alignment.TopEnd,
                            offset = IntOffset(
                                x = -horizontalSpacingPx,
                                y = verticalSpacingPx
                            ),
                            onDismissRequest = { showControlCenter = false },
                            properties = PopupProperties(
                                focusable = true,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            )
                        )
                    }
                }
            }
        }
    }
}