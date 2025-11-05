package com.android.tvlauncher3.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.theme.TVLauncher3Theme
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.screen.AppsScreen
import com.android.tvlauncher3.screen.HomeScreen
import com.android.tvlauncher3.screen.InputScreen
import com.android.tvlauncher3.utils.DisplayUtils
import com.android.tvlauncher3.utils.UIUtils
import com.android.tvlauncher3.view.SettingsPanel
import com.android.tvlauncher3.view.button.RoundButton
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置主题
        setTheme(android.R.style.Theme_Material_Wallpaper_NoTitleBar)
        // 隐藏状态栏和导航栏
        UIUtils.hideSystemBars(window)

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

            TVLauncher3Theme {
                val tag = "MainActivity"
                val tabs = viewModel.tabs
                val showSettingsPanel by viewModel.showSettingsPanel.collectAsState()
                val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    delay(100)
                    focusRequester.requestFocus()
                }

                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(20.dp)
                            .onSizeChanged { intSize ->
                                val heightDp = DisplayUtils.pixelToDp(baseContext, intSize.height)
                                viewModel.setTopBarHeight(heightDp)
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier
                                .focusRestorer()
                                .focusRequester(focusRequester),
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions, doesTabRowHaveFocus ->
                                TabRowDefaults.PillIndicator(
                                    currentTabPosition = tabPositions[selectedTabIndex],
                                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                                    activeColor = Color.White.copy(alpha = 0.5f),
                                    inactiveColor = Color.LightGray.copy(alpha = 0.5f)
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                val interactionSource = remember { MutableInteractionSource() }
                                val focusState = interactionSource.collectIsFocusedAsState()
                                val hoverState = interactionSource.collectIsHoveredAsState()
                                val bgColor by animateColorAsState(
                                    targetValue = if (focusState.value || hoverState.value) Color.White.copy(
                                        alpha = 0.25f
                                    ) else Color.Transparent,
                                    animationSpec = tween(durationMillis = 250)
                                )

                                key(index) {
                                    Tab(
                                        selected = selectedTabIndex == index,
                                        onFocus = {
                                            Log.i(tag, "Focused tab #$index.")
                                        },
                                        modifier = Modifier
                                            .background(color = bgColor, shape = CircleShape)
                                            .combinedClickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                enabled = true,
                                                role = Role.Tab,
                                                onClick = {
                                                    Log.i(tag, "Clicked tab #$index.")
                                                    viewModel.setSelectedTabIndex(index)
                                                }
                                            ),
                                        onClick = {
                                            Log.i(tag, "Clicked tab #$index by remote controller.")
                                            viewModel.setSelectedTabIndex(index)
                                        },
                                        colors = TabDefaults.pillIndicatorTabColors(
                                            contentColor = Color.White.copy(alpha = 0.75f),
                                            inactiveContentColor = Color.White.copy(alpha = 0.5f),
                                            selectedContentColor = Color.Black,
                                            focusedContentColor = Color.Black,
                                            focusedSelectedContentColor = Color.Black,
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
                                                tint = Color.White
                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = stringResource(tab.second),
                                                color = Color.White,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        RoundButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.settings,
                            onShortClick = {
                                viewModel.setShowSettingsPanel(true)
                            }
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            modifier = Modifier
                                .focusable(enabled = false)
                                .focusProperties { canFocus = false },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AndroidView(
                                factory = { context ->
                                    TextClock(context).apply {
                                        format12Hour = "yyyy-MM-dd"
                                        format24Hour = "yyyy-MM-dd"
                                        textSize = 18F
                                        setEnabled(false)
                                        setTextColor(android.graphics.Color.WHITE)
                                    }
                                },
                                modifier = Modifier
                                    .focusable(enabled = false)
                                    .focusProperties { canFocus = false }
                            )

                            AndroidView(
                                factory = { context ->
                                    TextClock(context).apply {
                                        format12Hour = "hh:mm:ss"
                                        format24Hour = "HH:mm:ss"
                                        textSize = 24F
                                        setEnabled(false)
                                        setTextColor(android.graphics.Color.WHITE)
                                    }
                                },
                                modifier = Modifier
                                    .focusable(enabled = false)
                                    .focusProperties { canFocus = false }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Transparent)
                            .padding(20.dp)
                    ) {
                        when (selectedTabIndex) {
                            0 -> HomeScreen(viewModel = viewModel)
                            1 -> AppsScreen(viewModel = viewModel)
                            2 -> InputScreen(viewModel = viewModel)
                            else -> viewModel.setSelectedTabIndex(0)
                        }
                    }
                }

                if (showSettingsPanel) {
                    SettingsPanel(
                        viewModel = viewModel,
                        onDismissRequest = {
                            viewModel.setShowSettingsPanel(false)
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}