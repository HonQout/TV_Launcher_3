package com.android.tvlauncher3.activity

import android.os.Bundle
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.PagesNavigation
import com.android.tvlauncher3.activity.ui.theme.TVLauncher3Theme
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.DisplayUtils
import com.android.tvlauncher3.utils.UIUtils
import com.android.tvlauncher3.view.SettingsPanel
import com.android.tvlauncher3.view.button.TopBarActionButton

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置主题
        setTheme(android.R.style.Theme_Material_Wallpaper_NoTitleBar)
        // 隐藏状态栏和导航栏
        UIUtils.hideSystemBars(window)

        setContent {
            val showSettingsPanel by viewModel.showSettingsPanel.collectAsState()
            val focusRequester = remember { FocusRequester() }

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
                            .fillMaxWidth()
                            .padding(20.dp)
                            .align(Alignment.TopEnd)
                            .onSizeChanged { intSize ->
                                val heightDp = DisplayUtils.pixelToDp(baseContext, intSize.height)
                                viewModel.setTopBarHeight(heightDp)
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopBarActionButton(
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
                            horizontalAlignment = Alignment.End
                        ) {
                            AndroidView(
                                factory = { context ->
                                    TextClock(context).apply {
                                        format12Hour = "yyyy-MM-dd"
                                        format24Hour = "yyyy-MM-dd"
                                        textSize = 20F
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
                                        textSize = 26F
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

                    PagesNavigation(
                        modifier = Modifier
                            .fillMaxSize(),
                        viewModel = viewModel
                    )
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