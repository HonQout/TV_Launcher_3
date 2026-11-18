package com.github.honqout.tvlauncher3.data.model.activity

data class ActivityRecord(
    var packageName: String,
    var activityName: String
) {
    fun getKey(): String {
        return "$packageName:$activityName"
    }
}