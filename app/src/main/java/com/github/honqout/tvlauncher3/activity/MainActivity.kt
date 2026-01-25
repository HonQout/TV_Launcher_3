package com.github.honqout.tvlauncher3.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Icon
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.activity.ui.theme.TVLauncher3Theme
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.InputViewModel
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.LauncherViewModel
import com.github.honqout.tvlauncher3.activity.ui.viewmodel.TimeViewModel
import com.github.honqout.tvlauncher3.constants.ColorConstants
import com.github.honqout.tvlauncher3.constants.NumberConstants
import com.github.honqout.tvlauncher3.constants.UIConstants
import com.github.honqout.tvlauncher3.utils.DisplayUtils
import com.github.honqout.tvlauncher3.utils.UIUtils
import com.github.honqout.tvlauncher3.view.button.IconButtonTv
import com.github.honqout.tvlauncher3.view.dialog.SettingsDialog
import com.github.honqout.tvlauncher3.view.screen.AppsScreen
import com.github.honqout.tvlauncher3.view.screen.HomeScreen
import com.github.honqout.tvlauncher3.view.screen.InputScreen
import kotlinx.coroutines.delay

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
        UIUtils.hideSystemBars(window)
        // Intercept back event

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
                val tabs = launcherViewModel.tabs
                val showSettingsDialog by launcherViewModel.showSettingsDialog.collectAsState()
                val selectedTabIndex by launcherViewModel.selectedTabIndex.collectAsState()
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    delay(100)
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
                            .padding(20.dp)
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
                                .background(ColorConstants.OnWallpaperContainer)
                                .focusRestorer()
                                .focusRequester(focusRequester),
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions, doesTabRowHaveFocus ->
                                TabRowDefaults.PillIndicator(
                                    currentTabPosition = tabPositions[selectedTabIndex],
                                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                                    modifier = Modifier,
                                    activeColor = ColorConstants.TabContainerColorActive,
                                    inactiveColor = ColorConstants.TabContainerColorInactive
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                val interactionSource = remember { MutableInteractionSource() }
                                val focusState = interactionSource.collectIsFocusedAsState()
                                val hoverState = interactionSource.collectIsHoveredAsState()
                                val bgColor by animateColorAsState(
                                    targetValue = if (focusState.value || hoverState.value)
                                        ColorConstants.TabContainerColorActive
                                    else Color.Transparent,
                                    animationSpec = tween(durationMillis = NumberConstants.ANIM_DURATION_MS)
                                )
                                val contentColor by animateColorAsState(
                                    targetValue = if (selectedTabIndex == index)
                                        ColorConstants.TabContentColorActive
                                    else if (focusState.value || hoverState.value)
                                        ColorConstants.TabContentColorHovered
                                    else ColorConstants.TabContentColorInactive
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
                                            selectedContentColor = ColorConstants.TabContentColorActive,
                                            focusedContentColor = ColorConstants.TabContentColorHovered,
                                            focusedSelectedContentColor = ColorConstants.TabContentColorActive,
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
                                                fontSize = UIConstants.FONT_SIZE_MEDIUM
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButtonTv(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.settings,
                            onShortClick = {
                                launcherViewModel.setShowSettingsScreen(true)
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
                                    color = ColorConstants.OnWallpaperContainer,
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

                    AnimatedVisibility(
                        visible = showSettingsDialog,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    ) {
                        SettingsDialog(
                            launcherViewModel = launcherViewModel,
                            timeViewModel = timeViewModel,
                            onDismissRequest = {
                                launcherViewModel.setShowSettingsScreen(false)
                            }
                        )
                    }
                }
            }
        }
    }
}