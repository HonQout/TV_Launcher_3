package com.github.honqout.tvlauncher3.data.model.settings

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class SingleChoiceSettingModel(
    override val key: String,
    override var title: ResourceStr,
    override var iconRes: Int?,
    override var summary: ResourceStr?,
    var choices: Array<Choice>,
    var chosen: Int = NOT_CHOSEN
) : SettingModel {
    companion object {
        const val NOT_CHOSEN: Int = -1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SingleChoiceSettingModel

        if (iconRes != other.iconRes) return false
        if (chosen != other.chosen) return false
        if (key != other.key) return false
        if (title != other.title) return false
        if (summary != other.summary) return false
        if (!choices.contentEquals(other.choices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconRes.hashCode()
        result = 31 * result + chosen
        result = 31 * result + key.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + choices.contentHashCode()
        return result
    }

    fun updateChosen(newValue: Int): Boolean {
        return when (newValue) {
            NOT_CHOSEN -> {
                chosen = newValue
                true
            }
            in 0..choices.size -> {
                chosen = newValue
                true
            }
            else -> {
                false
            }
        }
    }
}