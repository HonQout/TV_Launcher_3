package com.github.honqout.tvlauncher3.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.util.Log
import com.github.honqout.tvlauncher3.data.model.activity.ActivityModel

class AppRepository {
    companion object {
        private const val TAG: String = "AppRepository"
    }

    private var launcherApps: LauncherApps
    private var callback: LauncherApps.Callback
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    constructor(context: Context, callback: LauncherApps.Callback) {
        launcherApps = context
            .applicationContext
            .getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        this.callback = callback
    }

    fun register(): Boolean {
        launcherApps.registerCallback(callback, mainHandler)
        return true
    }

    fun unregister(): Boolean {
        launcherApps.unregisterCallback(callback)
        return true
    }

    /**
     * Get all activity models of activities specified [Intent.ACTION_MAIN] or
     * [Intent.CATEGORY_LAUNCHER] of the current user.
     */
    fun getActivityModelList(
        context: Context,
        pkgName: String?
    ): List<ActivityModel> {
        val resultList: MutableList<ActivityModel> = mutableListOf()
        val userHandle: UserHandle = Process.myUserHandle()
        val list: List<LauncherActivityInfo> = launcherApps.getActivityList(pkgName, userHandle)
        for (info in list) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val activityInfo: ActivityInfo = info.activityInfo
                val activityModel: ActivityModel =
                    ActivityModel.fromActivityInfo(context, activityInfo)
                resultList.add(activityModel)
            } else {
                val packageName: String = info.componentName.packageName
                val activityName = info.componentName.className
                val activityModel: ActivityModel =
                    ActivityModel.fromComponents(context, packageName, activityName)
                resultList.add(activityModel)
            }
        }
        return resultList
    }

    /**
     * Get the activity model with the given `packageName` and `activityName`.
     */
    fun getActivityModel(
        context: Context,
        packageName: String,
        activityName: String
    ): ActivityModel? {
        if (packageName.isEmpty() || activityName.isEmpty()) {
            Log.e(TAG, "Cannot get activity model. The given packageName or activityName is null.")
            return null
        }

        val userHandle: UserHandle = Process.myUserHandle()
        val list: List<LauncherActivityInfo> = launcherApps.getActivityList(packageName, userHandle)
        for (info in list) {
            if (info.componentName.className == activityName) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityModel.fromActivityInfo(context, info.activityInfo)
                } else {
                    ActivityModel.fromComponents(context, packageName, activityName)
                }
            }
        }
        return null
    }
}