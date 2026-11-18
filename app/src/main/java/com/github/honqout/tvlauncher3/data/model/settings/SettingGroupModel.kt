package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class SettingGroupModel(
    override val key: String,
    override var title: ResourceStr,
    override var iconRes: Int? = null,
    override var summary: ResourceStr? = null,
    val settingList: List<SettingModel>
) : SettingModel