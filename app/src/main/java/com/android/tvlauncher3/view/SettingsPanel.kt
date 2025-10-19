package com.android.tvlauncher3.view

import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.SettingsActionButton
import com.android.tvlauncher3.view.text.DateAndWeekdayText
import com.android.tvlauncher3.view.text.TimeText

@Composable
fun SettingsPanel(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit = {}
) {
    val tag = "SettingsPanel"
    val numColumns = 2
    val bgColor = Color.Black.copy(alpha = 0.5f)
    val context = LocalContext.current
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(20.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f)
                    .background(color = Color.Transparent)
                    .align(Alignment.TopEnd)
                    .focusable(enabled = false)
            ) {
                Row(
                    modifier = Modifier
                        .height(topBarHeight.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeText(
                        viewModel = viewModel,
                        modifier = Modifier,
                        color = Color.White,
                        fontSize = 30.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    DateAndWeekdayText(
                        viewModel = viewModel,
                        modifier = Modifier,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(numColumns),
                    modifier = Modifier
                        .focusGroup(),
                    state = lazyGridState,
                    contentPadding = PaddingValues(all = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = true
                ) {
                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.settings,
                            titleRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    context,
                                    Settings.ACTION_SETTINGS
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_settings_24,
                            contentDescriptionRes = R.string.tv_settings,
                            titleRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    context,
                                    "com.android.tv.settings",
                                    "com.android.tv.settings.MainSettings",
                                    true
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_wifi_24,
                            contentDescriptionRes = R.string.wlan,
                            titleRes = R.string.wlan,
                            descriptionRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    context,
                                    Settings.ACTION_WIFI_SETTINGS
                                )
                            },
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_bluetooth_24,
                            contentDescriptionRes = R.string.bluetooth,
                            titleRes = R.string.bluetooth,
                            descriptionRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    context,
                                    Settings.ACTION_BLUETOOTH_SETTINGS
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_web_24,
                            contentDescriptionRes = R.string.internet,
                            titleRes = R.string.internet,
                            descriptionRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    context,
                                    "com.android.tv.settings",
                                    "com.android.tv.settings.connectivity.NetworkActivity",
                                    true
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_settings_remote_24,
                            contentDescriptionRes = R.string.accessory,
                            titleRes = R.string.accessory,
                            descriptionRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    context,
                                    "com.android.tv.settings",
                                    "com.android.tv.settings.accessories.AddAccessoryActivity",
                                    true
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_speaker_24,
                            contentDescriptionRes = R.string.sound,
                            titleRes = R.string.sound,
                            descriptionRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    context,
                                    Settings.ACTION_SOUND_SETTINGS
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_speaker_24,
                            contentDescriptionRes = R.string.sound,
                            titleRes = R.string.sound,
                            descriptionRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    context,
                                    "com.android.tv.settings",
                                    "com.android.tv.settings.device.sound.SoundActivity",
                                    true
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_tv_24,
                            contentDescriptionRes = R.string.display,
                            titleRes = R.string.display,
                            descriptionRes = R.string.settings,
                            onShortClick = {
                                IntentUtils.launchSettingsActivity(
                                    context,
                                    Settings.ACTION_DISPLAY_SETTINGS
                                )
                            }
                        )
                    }

                    item {
                        SettingsActionButton(
                            iconRes = R.drawable.baseline_settings_system_daydream_24,
                            contentDescriptionRes = R.string.screen_saver,
                            titleRes = R.string.screen_saver,
                            descriptionRes = R.string.tv_settings,
                            onShortClick = {
                                IntentUtils.launchActivity(
                                    context,
                                    "com.android.tv.settings",
                                    "com.android.tv.settings.device.display.daydream.DaydreamActivity",
                                    true
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}