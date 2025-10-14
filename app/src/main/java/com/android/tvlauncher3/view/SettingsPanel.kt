package com.android.tvlauncher3.view

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.SettingsActionButton

@Composable
fun SettingsPanel(
    context: Context,
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit = {}
) {
    val tag = "SettingsPanel"
    val numColumns = 2
    val lazyGridState = rememberLazyGridState()
    val showSettingsPanel by viewModel.showSettingsPanel.collectAsState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    BackHandler {
        Log.i(tag, "Pressed back button.")
        onDismissRequest()
    }

    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .fillMaxSize()
            .clickable {
                onDismissRequest()
            }
            .focusGroup()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 1000.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .padding(20.dp)
                .align(Alignment.TopEnd)
                .focusGroup()
                .clickable(enabled = false) {

                }
                .hoverable(interactionSource = interactionSource, enabled = false)
        ) {
            Row(
                modifier = Modifier.height(topBarHeight.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier,
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
                        textRes = R.string.settings,
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
                        textRes = R.string.tv_settings,
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier,
                state = lazyGridState,
                contentPadding = PaddingValues(all = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = true
            ) {
                item {
                    SettingsActionButton(
                        iconRes = R.drawable.baseline_wifi_24,
                        contentDescriptionRes = R.string.wifi,
                        textRes = R.string.wifi,
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
                        textRes = R.string.bluetooth,
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
                        iconRes = R.drawable.baseline_speaker_24,
                        contentDescriptionRes = R.string.sound,
                        textRes = R.string.sound,
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
                        iconRes = R.drawable.baseline_tv_24,
                        contentDescriptionRes = R.string.display,
                        textRes = R.string.display,
                        onShortClick = {
                            IntentUtils.launchSettingsActivity(
                                context,
                                Settings.ACTION_DISPLAY_SETTINGS
                            )
                        }
                    )
                }
            }
        }
    }
}