package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

sealed interface SettingModel {
    val key: String
    var title: ResourceStr
    var iconRes: Int?
    var summary: ResourceStr?
}