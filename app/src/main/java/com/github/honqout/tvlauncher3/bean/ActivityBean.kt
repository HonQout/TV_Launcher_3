package com.github.honqout.tvlauncher3.bean

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.IconType

class ActivityBean {
    var resolveInfo: ResolveInfo? = null
    var packageName: String = ""
    var activityName: String = ""
    var label: String = ""
    var icon: Drawable? = null
    var iconType: IconType = IconType.Icon

    constructor(context: Context, resolveInfo: ResolveInfo) {
        this.resolveInfo = resolveInfo
        packageName = ApplicationUtils.getPackageName(resolveInfo) ?: ""
        activityName = ApplicationUtils.getActivityName(resolveInfo)
        label = ApplicationUtils.getActivityLabel(context, resolveInfo)
        val (icon, iconType) = ApplicationUtils.getApplicationBanner(context, resolveInfo)
        this.icon = icon
        this.iconType = iconType
    }

    fun getIcon(context: Context): Drawable {
        return icon ?: context.packageManager.defaultActivityIcon
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }
}