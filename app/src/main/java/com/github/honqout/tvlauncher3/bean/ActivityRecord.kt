package com.github.honqout.tvlauncher3.bean

import android.content.pm.ResolveInfo
import com.github.honqout.tvlauncher3.utils.ApplicationUtils

open class ActivityRecord {
    var packageName: String = ""
    var activityName: String = ""

    constructor(packageName: String, activityName: String) {
        this.packageName = packageName
        this.activityName = activityName
    }

    constructor(resolveInfo: ResolveInfo) {
        packageName = ApplicationUtils.getPackageName(resolveInfo)
        activityName = ApplicationUtils.getActivityName(resolveInfo)
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }
}