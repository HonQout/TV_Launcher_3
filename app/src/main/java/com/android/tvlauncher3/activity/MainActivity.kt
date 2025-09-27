package com.android.tvlauncher3.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.PagesNavigation
import com.android.tvlauncher3.activity.ui.theme.TVLauncher3Theme
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.DisplayUtils
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.TopBarActionButton

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val packageBroadcastReceiver: PackageBroadcastReceiver = PackageBroadcastReceiver()

    @OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置主题
        setTheme(android.R.style.Theme_Material_Wallpaper_NoTitleBar)
        // 隐藏状态栏和导航栏
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // 注册监听应用状态的广播接收器
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        val receiverFlags = ContextCompat.RECEIVER_EXPORTED
        ContextCompat.registerReceiver(
            baseContext,
            packageBroadcastReceiver,
            intentFilter,
            receiverFlags
        )

        // 设置内容
        setContent {
            val viewModel: MainViewModel = viewModel()

            LaunchedEffect(Unit) {

            }

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
                LaunchedEffect(Unit) {

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
                            iconRes = R.drawable.baseline_wifi_24,
                            contentDescriptionRes = R.string.wifi,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    baseContext,
                                    Settings.ACTION_WIFI_SETTINGS
                                )
                            },
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        TopBarActionButton(
                            iconRes = R.drawable.baseline_bluetooth_24,
                            contentDescriptionRes = R.string.bluetooth,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    baseContext,
                                    Settings.ACTION_BLUETOOTH_SETTINGS
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        TopBarActionButton(
                            iconRes = R.drawable.baseline_speaker_24,
                            contentDescriptionRes = R.string.sound,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    baseContext,
                                    Settings.ACTION_SOUND_SETTINGS
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        TopBarActionButton(
                            iconRes = R.drawable.baseline_tv_24,
                            contentDescriptionRes = R.string.display,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    baseContext,
                                    Settings.ACTION_DISPLAY_SETTINGS
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        TopBarActionButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    baseContext,
                                    Settings.ACTION_SETTINGS
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        TopBarActionButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    baseContext,
                                    "com.android.tv.settings",
                                    ".MainSettings",
                                    true
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

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
                        context = baseContext,
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 反注册广播接收器
        this.unregisterReceiver(packageBroadcastReceiver)
    }

    inner class PackageBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                Log.i(TAG, "Received null message.")
            } else {
                val action: String = intent.action ?: "null"
                Log.i(TAG, "Received message: $action")
                when (action) {
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        if (!isReplacing) {
                            var packageName = "null"
                            if (intent.data != null) {
                                packageName = intent.data?.schemeSpecificPart ?: "null"
                            }
                            Log.i(TAG, "Package $packageName has been added.")
                            viewModel.addItems(packageName)
                        }
                    }

                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        if (!isReplacing) {
                            var packageName = "null"
                            if (intent.data != null) {
                                packageName = intent.data?.schemeSpecificPart ?: "null"
                            }
                            Log.i(TAG, "Package $packageName has been removed.")
                            viewModel.removeItems(packageName)
                        }
                    }

                    Intent.ACTION_PACKAGE_REPLACED -> {
                        var packageName = "null"
                        if (intent.data != null) {
                            packageName = intent.data?.schemeSpecificPart ?: "null"
                        }
                        Log.i(TAG, "Package $packageName has been replaced.")
                        viewModel.replaceItems(packageName)
                    }

                    else -> {
                        Log.e(TAG, "Received irrelevant message.")
                    }
                }
            }
        }
    }
}