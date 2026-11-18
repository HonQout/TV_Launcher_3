package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class SwitchSettingModel(
    override val key: String,
    override var title: ResourceStr,
    override var iconRes: Int?,
    override var summary: ResourceStr?,
    var value: Boolean = false
) : SettingModel {
    fun invertSwitch() {
        this.value = !this.value
    }
}