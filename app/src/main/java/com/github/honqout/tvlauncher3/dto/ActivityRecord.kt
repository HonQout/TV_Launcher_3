package com.github.honqout.tvlauncher3.dto

import android.content.pm.ResolveInfo
import com.github.honqout.tvlauncher3.utils.ApplicationUtils

data class ActivityRecord(
    var packageName: String,
    var activityName: String
) {
    companion object {
        fun fromResolveInfo(resolveInfo: ResolveInfo): ActivityRecord {
            return ActivityRecord(
                packageName = ApplicationUtils.getPackageName(resolveInfo),
                activityName = ApplicationUtils.getActivityName(resolveInfo)
            )
        }
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }
}