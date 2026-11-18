package com.github.honqout.tvlauncher3.ui.settings.components.screen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.data.model.settings.InfoSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.InputSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SettingGroupModel
import com.github.honqout.tvlauncher3.data.model.settings.SettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SingleChoiceSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SlideSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SwitchSettingModel
import com.github.honqout.tvlauncher3.ui.settings.components.item.InfoSettingRow
import com.github.honqout.tvlauncher3.ui.settings.components.item.InputSettingRow
import com.github.honqout.tvlauncher3.ui.settings.components.item.SingleChoiceSettingRow
import com.github.honqout.tvlauncher3.ui.settings.components.item.SlideSettingRow
import com.github.honqout.tvlauncher3.ui.settings.components.item.SwitchSettingRow
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingList: List<SettingModel> = emptyList(),
    onItemClicked: (SettingModel) -> Unit = {},
    onItemSwitched: (String, Boolean) -> Unit = { _, _ -> },
    onItemChosen: (String, Int) -> Unit = { _, _ -> },
    onItemSlid: (String, Int) -> Unit = { _, _ -> },
    onItemInput: (String, String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        items(settingList, key = { it.key }) { item ->
            when (item) {
                is InfoSettingModel -> {
                    InfoSettingRow(
                        item = item,
                        onClick = { onItemClicked(item) }
                    )
                }

                is SwitchSettingModel -> {
                    SwitchSettingRow(
                        item = item,
                        onSwitch = { onItemSwitched(item.key, it) }
                    )
                }

                is SingleChoiceSettingModel -> {
                    SingleChoiceSettingRow(
                        item = item,
                        onClick = { onItemClicked(item) },
                        onChoose = { onItemChosen(item.key, it) }
                    )
                }

                is SlideSettingModel -> {
                    SlideSettingRow(
                        item = item,
                        onClick = { onItemClicked(item) },
                        onSlide = { onItemSlid(item.key, it) }
                    )
                }

                is InputSettingModel -> {
                    InputSettingRow(
                        item = item,
                        onClick = { onItemClicked(item) },
                        onInput = { onItemInput(item.key, it) }
                    )
                }

                is SettingGroupModel -> {
                    Text(
                        text = item.title.asComposableString(),
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
                            .focusable(enabled = false),
                        color = Color.LightGray,
                        fontSize = FONT_SIZE_S,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )

                    item.settingList.forEach { subItem ->
                        when (subItem) {
                            is InfoSettingModel -> {
                                InfoSettingRow(
                                    item = subItem,
                                    onClick = { onItemClicked(subItem) }
                                )
                            }

                            is SwitchSettingModel -> {
                                SwitchSettingRow(
                                    item = subItem,
                                    onSwitch = { onItemSwitched(subItem.key, it) }
                                )
                            }

                            is SingleChoiceSettingModel -> {
                                SingleChoiceSettingRow(
                                    item = subItem,
                                    onClick = { onItemClicked(subItem) },
                                    onChoose = { onItemChosen(subItem.key, it) }
                                )
                            }

                            is SlideSettingModel -> {
                                SlideSettingRow(
                                    item = subItem,
                                    onClick = { onItemClicked(subItem) },
                                    onSlide = { onItemSlid(subItem.key, it) }
                                )
                            }

                            is InputSettingModel -> {
                                InputSettingRow(
                                    item = subItem,
                                    onClick = { onItemClicked(subItem) },
                                    onInput = { onItemInput(subItem.key, it) }
                                )
                            }

                            is SettingGroupModel -> {
                                // Prevents settings group from being nested
                            }
                        }
                    }
                }
            }
        }
    }
}