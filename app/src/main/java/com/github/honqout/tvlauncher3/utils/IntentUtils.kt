package com.github.honqout.tvlauncher3.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri

class IntentUtils {
    companion object {
        private const val TAG: String = "IntentUtils"

        enum class LaunchIntentResult {
            SUCCESS,
            URI_IS_EMPTY,
            NO_MATCHING_ACTIVITY,
            REQUIRE_EXTRA_PERMISSION
        }

        enum class LaunchActivityResult {
            SUCCESS,
            NOT_FOUND,
            NOT_EXPORTED,
            REQUIRE_EXTRA_PERMISSION
        }

        interface OnRequestUninstallApp {
            fun uninstallThisApp()
            fun uninstallSystemApp(packageName: String)
            fun uninstallUpdatedSystemApp(packageName: String)
            fun uninstallUserApp(packageName: String)
            fun onError(packageName: String)
        }

        /**
         * Launch an action which is defined by system.
         */
        fun launchAction(
            context: Context,
            action: String,
            newTask: Boolean
        ): LaunchIntentResult {
            if (action.isEmpty()) {
                Log.e(TAG, "Cannot launch intent because the given action is empty.")
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(action).apply {
                if (newTask) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Cannot launch intent because no activity can handle it.", e)
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }

        /**
         * Launch the specified activity.
         */
        fun launchActivity(
            context: Context,
            intent: Intent
        ): LaunchActivityResult {
            val activityInfo = intent.resolveActivityInfo(
                context.packageManager,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (activityInfo != null) {
                if (!activityInfo.exported) {
                    Log.e(TAG, "Cannot launch activity because it is not exported.")
                    return LaunchActivityResult.NOT_EXPORTED
                } else if (!activityInfo.permission.isNullOrEmpty()) {
                    Log.e(TAG, "Activity is not exported or needs extra permission to start.")
                    return LaunchActivityResult.REQUIRE_EXTRA_PERMISSION
                } else {
                    try {
                        context.startActivity(intent)
                        return LaunchActivityResult.SUCCESS
                    } catch (e: ActivityNotFoundException) {
                        Log.e(TAG, "Cannot find requested activity.", e)
                        return LaunchActivityResult.NOT_FOUND
                    }
                }
            } else {
                Log.e(TAG, "ActivityInfo is null.")
                return LaunchActivityResult.NOT_FOUND
            }
        }

        /**
         * Launch the specified activity.
         */
        fun launchActivity(
            context: Context,
            packageName: String,
            activityName: String,
            newTask: Boolean
        ): LaunchActivityResult {
            val intent = Intent().apply {
                setClassName(packageName, activityName)
                if (newTask) {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            return launchActivity(context, intent)
        }

        /**
         * Launch an app's launcher activity. This will launch the Leanback Launch Intent which is
         * designed for TV prior to the Launch Intent which is designed for phone and tablet. If
         * both intents are null, LaunchActivityIntent.NOT_FOUND will be returned.
         */
        fun launchApp(
            context: Context,
            packageName: String,
            newTask: Boolean
        ): LaunchActivityResult {
            val pm = context.packageManager
            val leanbackIntent = pm.getLeanbackLaunchIntentForPackage(packageName)
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (leanbackIntent != null) {
                if (newTask) {
                    leanbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return launchActivity(context, leanbackIntent)
            } else if (intent != null) {
                if (newTask) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return launchActivity(context, intent)
            } else {
                return LaunchActivityResult.NOT_FOUND
            }
        }

        /**
         * Request to uninstall an application.
         */
        fun requestUninstallApp(
            context: Context,
            packageName: String
        ): LaunchIntentResult {
            if (packageName.isEmpty()) {
                Log.e(TAG, "Cannot uninstall app. The given packageName is empty.")
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(Intent.ACTION_DELETE).apply {
                setData(("package:$packageName").toUri())
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Failed to uninstall app. No activity can handle it.", e)
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }

        /**
         * Open the Application Details Page of the given application in Settings (com.android.settings).
         */
        fun launchApplicationDetailsSettings(
            context: Context,
            packageName: String
        ): LaunchIntentResult {
            if (packageName.isEmpty()) {
                Log.e(
                    TAG,
                    "Cannot launch Settings. The given packageName is empty."
                )
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                setData(Uri.fromParts("package", packageName, null))
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(
                    TAG,
                    "Failed to launch Settings. Requested package cannot be found.",
                    e
                )
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }

        fun openAppInMarket(
            context: Context,
            packageName: String,
            flags: Int
        ): LaunchIntentResult {
            if (packageName.isEmpty()) {
                Log.e(
                    TAG,
                    "Cannot open detail page of this app in app market. " +
                            "The given packageName is empty."
                )
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setData(("market://details?id=$packageName").toUri())
                setFlags(flags)
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(
                    TAG,
                    "Failed to open detail page of this app in app market. " +
                            "No activity can open this uri.",
                    e
                )
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }

        fun openWebsite(
            context: Context,
            address: String,
            flags: Int
        ): LaunchIntentResult {
            if (address.isEmpty()) {
                Log.e(
                    TAG,
                    "Cannot open website. The given address is empty."
                )
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setData(address.toUri())
                setFlags(flags)
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(
                    TAG,
                    "Cannot open website. No activity can open this uri.",
                    e
                )
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }
    }
}