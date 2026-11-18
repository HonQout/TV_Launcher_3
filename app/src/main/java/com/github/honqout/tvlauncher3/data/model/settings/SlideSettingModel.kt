package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class SlideSettingModel(
    override val key: String,
    override var title: ResourceStr,
    override var iconRes: Int?,
    override var summary: ResourceStr?,
    var range: IntRange = IntRange(0, 100),
    var step: Int = 1,
    var progress: Float = 50.0f
) : SettingModel {
    fun updateProgress(newValue: Float): Boolean {
        return if (progress in range.first.toFloat()..range.last.toFloat()) {
            progress = newValue
            true
        } else {
            false
        }
    }
}