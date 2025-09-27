package com.android.tvlauncher3.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.net.toUri

class IntentUtils {
    companion object {
        private const val TAG: String = "IntentUtils"

        fun launchActivity(
            context: Context,
            intent: Intent
        ): Boolean {
            val activityInfo = intent.resolveActivityInfo(
                context.packageManager,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (activityInfo != null) {
                if (activityInfo.exported && TextUtils.isEmpty(activityInfo.permission)) {
                    try {
                        context.startActivity(intent)
                        return true
                    } catch (e: ActivityNotFoundException) {
                        Log.e(TAG, "Cannot find requested activity.", e)
                        return false
                    }
                } else {
                    Log.e(TAG, "Activity is not exported or needs extra permission to start.")
                    return false
                }
            } else {
                Log.e(TAG, "ActivityInfo is null.")
                return false
            }
        }

        fun launchActivity(
            context: Context,
            packageName: String,
            activityName: String,
            newTask: Boolean
        ): Boolean {
            val intent = Intent().apply {
                setClassName(packageName, activityName)
                if (newTask) {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            return launchActivity(context, intent)
        }

        fun launchApp(context: Context, packageName: String, newTask: Boolean): Boolean {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent == null) {
                Log.e(TAG, "Intent is null.")
                return false
            } else {
                if (newTask) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return launchActivity(context, intent)
            }
        }

        fun launchSettingsActivity(context: Context, activity: String): Boolean {
            if (!activity.startsWith("android.settings.")) {
                Log.e(TAG, "Required activity is not a settings activity.")
                return false
            }
            val intent = Intent(activity).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                Log.i(TAG, "Prepare to start activity $activity")
                context.startActivity(intent)
                return true
            } else {
                Log.e(TAG, "Intent is null.")
                return false
            }
        }

        fun requestUninstallApp(context: Context, packageName: String): Boolean {
            if (TextUtils.isEmpty(packageName)) {
                return false
            }
            val intent = Intent(Intent.ACTION_DELETE).apply {
                setData(("package:$packageName").toUri())
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
                return true
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Failed to uninstall application. Cannot find requested activity.", e)
                return false
            }
        }

        fun openApplicationSettingsPage(context: Context, packageName: String): Boolean {
            if (TextUtils.isEmpty(packageName)) {
                return false
            }
            val intent = Intent().apply {
                setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                setData(Uri.fromParts("package", packageName, null))
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
                return true
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Failed to launch Settings. Cannot find requested activity.", e)
                return false
            }
        }
    }
}