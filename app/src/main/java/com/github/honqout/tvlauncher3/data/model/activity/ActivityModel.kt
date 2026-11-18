package com.github.honqout.tvlauncher3.data.model.activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.annotation.ColorInt
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.github.honqout.tvlauncher3.utils.DrawableUtils

data class ActivityModel(
    var packageName: String,
    var activityName: String,
    var label: String = "",
    var iconType: IconType = IconType.Icon,
    @param:ColorInt var color: Int = Color.TRANSPARENT,
    var versionName: String = "",
    var versionCode: Int = 0,
    var appLabel: String = ""
) {
    companion object {
        fun fromComponents(
            context: Context,
            packageName: String,
            activityName: String
        ): ActivityModel {
            val activityInfo: ActivityInfo? =
                ApplicationUtils.getActivityInfo(context, packageName, activityName)
            val label: String = ApplicationUtils.getActivityLabel(context, activityInfo)
            val (type, icon) = ApplicationUtils.getApplicationIconPair(context, packageName)
            val bgColor = DrawableUtils.getDominantColorOfAppIcon(icon)
            val versionName = ApplicationUtils.getVersionName(context, packageName) ?: ""
            val versionCode = ApplicationUtils.getVersionCode(context, packageName) ?: 0
            val appLabel = ApplicationUtils.getApplicationLabel(context, packageName) ?: ""
            return ActivityModel(
                packageName = packageName,
                activityName = activityName,
                label = label,
                iconType = type,
                color = bgColor,
                versionName = versionName,
                versionCode = versionCode,
                appLabel = appLabel
            )
        }

        fun fromActivityInfo(context: Context, activityInfo: ActivityInfo): ActivityModel {
            val packageName: String = activityInfo.packageName
            val activityName: String = activityInfo.name
            val label = ApplicationUtils.getActivityLabel(context, activityInfo)
            val (type, icon) = ApplicationUtils.getApplicationIconPair(context, packageName)
            val bgColor = DrawableUtils.getDominantColorOfAppIcon(icon)
            val versionName = ApplicationUtils.getVersionName(context, packageName) ?: ""
            val versionCode = ApplicationUtils.getVersionCode(context, packageName) ?: 0
            val appLabel = ApplicationUtils.getApplicationLabel(context, packageName) ?: ""
            return ActivityModel(
                packageName = packageName,
                activityName = activityName,
                label = label,
                iconType = type,
                color = bgColor,
                versionName = versionName,
                versionCode = versionCode,
                appLabel = appLabel
            )
        }
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }

    fun getFullVersion(): String {
        return "$versionName ($versionCode)"
    }

    fun shouldShowBelongToHint(): Boolean {
        return label.isNotEmpty() && appLabel.isNotEmpty() && label != appLabel
    }

    fun toActivityRecord(): ActivityRecord {
        return ActivityRecord(packageName, activityName)
    }
}