package com.github.honqout.tvlauncher3.bean

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import androidx.annotation.ColorInt
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.ApplicationType
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.IconType
import com.github.honqout.tvlauncher3.utils.DrawableUtils

class ActivityBean : ActivityRecord {
    var label: String = ""
    var showBelongToHint: Boolean = false
    var appType: ApplicationType = ApplicationType.UNKNOWN
    var iconType: IconType = IconType.Icon

    @ColorInt
    var color: Int = Color.TRANSPARENT

    constructor(context: Context, resolveInfo: ResolveInfo) : super(resolveInfo) {
        label = ApplicationUtils.getActivityLabel(context, resolveInfo)
        showBelongToHint =
            ApplicationUtils.shouldShowBelongToHint(context, packageName, activityName)
        appType = ApplicationUtils.getApplicationType(context, packageName)
        val (iconType, icon) = ApplicationUtils.getActivityIconPair(
            context,
            packageName,
            activityName
        )
        this.iconType = iconType
        color = DrawableUtils.getBackgroundColorFromAppIcon(icon)
    }
}