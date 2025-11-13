package com.github.honqout.tvlauncher3.bean

class ActivityRecord {
    var packageName: String = ""
    var activityName: String = ""

    constructor(packageName: String, activityName: String) {
        this.packageName = packageName
        this.activityName = activityName
    }

    fun getKey(): String {
        return "$packageName:$activityName"
    }
}