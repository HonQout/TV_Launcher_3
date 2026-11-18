package com.github.honqout.tvlauncher3.ui.launcher.components.controlcenter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.ui.launcher.components.button.SettingsActionButton
import com.github.honqout.tvlauncher3.ui.settings.activity.SettingsActivity
import com.github.honqout.tvlauncher3.ui.theme.ControlCenterContainer
import com.github.honqout.tvlauncher3.ui.theme.PADDING_LIST_CONTENT_EDGE
import com.github.honqout.tvlauncher3.ui.theme.SETTINGS_ACTION_BUTTON_WIDTH
import com.github.honqout.tvlauncher3.ui.theme.SPACE_LIST_CONTENT_HORIZONTAL
import com.github.honqout.tvlauncher3.ui.theme.SPACE_LIST_CONTENT_VERTICAL
import com.github.honqout.tvlauncher3.utils.IntentUtils

@Composable
fun ControlCenter(
    alignment: Alignment = Alignment.TopStart,
    offset: IntOffset = IntOffset.Zero,
    onDismissRequest: () -> Unit = {},
    properties: PopupProperties = PopupProperties(
        focusable = true,
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    )
) {
    val tag = "ControlCenter"
    val context = LocalContext.current
    val columnCount = 2
    val lazyGridState = rememberLazyGridState()
    val gridWidth =
        2 * SETTINGS_ACTION_BUTTON_WIDTH + SPACE_LIST_CONTENT_HORIZONTAL + 2 * PADDING_LIST_CONTENT_EDGE

    BackHandler {
        Log.i(tag, "Pressed back button.")
        onDismissRequest()
    }

    Popup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            modifier = Modifier
                .width(gridWidth)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = ControlCenterContainer,
            tonalElevation = 0.dp
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                state = lazyGridState,
                contentPadding = PaddingValues(PADDING_LIST_CONTENT_EDGE),
                verticalArrangement = Arrangement.spacedBy(SPACE_LIST_CONTENT_VERTICAL),
                horizontalArrangement = Arrangement.spacedBy(SPACE_LIST_CONTENT_HORIZONTAL),
                userScrollEnabled = true
            ) {
                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_settings_24,
                        contentDescriptionRes = R.string.settings,
                        titleRes = R.string.settings,
                        onShortClick = {
                            IntentUtils.launchAction(
                                context,
                                Settings.ACTION_SETTINGS,
                                true
                            )
                        }
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_settings_24,
                        contentDescriptionRes = R.string.launcher_settings,
                        titleRes = R.string.launcher_settings,
                        onShortClick = {
                            val intent = Intent(
                                context.applicationContext,
                                SettingsActivity::class.java
                            )
                            try {
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Log.e(
                                    tag,
                                    "Failed to start activity. Cannot find required activity.",
                                    e
                                )
                            }

                        }
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_web_24,
                        contentDescriptionRes = R.string.network_and_internet,
                        titleRes = R.string.network_and_internet,
                        onShortClick = {
                            IntentUtils.launchAction(
                                context,
                                Settings.ACTION_WIFI_SETTINGS,
                                true
                            )
                        },
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_tv_24,
                        contentDescriptionRes = R.string.display_and_sound,
                        titleRes = R.string.display_and_sound,
                        onShortClick = {
                            IntentUtils.launchAction(
                                context,
                                Settings.ACTION_SOUND_SETTINGS,
                                true
                            )
                        }
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_apps_24,
                        contentDescriptionRes = R.string.apps,
                        titleRes = R.string.apps,
                        onShortClick = {
                            IntentUtils.launchAction(
                                context,
                                Settings.ACTION_APPLICATION_SETTINGS,
                                true
                            )
                        }
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_accessibility_new_24,
                        contentDescriptionRes = R.string.accessibility,
                        titleRes = R.string.accessibility,
                        onShortClick = {
                            IntentUtils.launchAction(
                                context,
                                Settings.ACTION_ACCESSIBILITY_SETTINGS,
                                true
                            )
                        }
                    )
                }

                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_settings_system_daydream_24,
                        contentDescriptionRes = R.string.screen_saver,
                        titleRes = R.string.screen_saver,
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