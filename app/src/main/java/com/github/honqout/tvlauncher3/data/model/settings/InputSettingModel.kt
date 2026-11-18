package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class InputSettingModel(
    override val key: String,
    override var title: ResourceStr,
    override var iconRes: Int?,
    override var summary: ResourceStr?,
    var placeHolder: ResourceStr = ResourceStr.Str(value = ""),
    var inputText: String = ""
) : SettingModel