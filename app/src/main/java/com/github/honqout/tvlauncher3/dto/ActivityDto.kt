package com.github.honqout.tvlauncher3.dto

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import androidx.annotation.ColorInt
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.github.honqout.tvlauncher3.utils.DrawableUtils

data class ActivityDto(
    var packageName: String,
    var activityName: String,
    var label: String = "",
    var iconType: IconType = IconType.Icon,
    @param:ColorInt var color: Int = Color.TRANSPARENT
) {
    companion object {
        fun fromResolveInfo(context: Context, resolveInfo: ResolveInfo): ActivityDto {
            val packageName = ApplicationUtils.getPackageName(resolveInfo)
            val activityName = ApplicationUtils.getActivityName(resolveInfo)
            val (iconType, icon) = ApplicationUtils.getActivityIconPair(
                context,
                packageName,
                activityName
            )
            return ActivityDto(
                packageName = packageName,
                activityName = activityName,
                label = ApplicationUtils.getActivityLabel(context, resolveInfo),
                iconType = iconType,
                color = DrawableUtils.getBackgroundColorFromAppIcon(icon)
            )
        }
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }

    fun toActivityRecord(): ActivityRecord {
        return ActivityRecord(packageName, activityName)
    }
}