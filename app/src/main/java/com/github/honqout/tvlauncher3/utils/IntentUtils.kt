package com.github.honqout.tvlauncher3.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.github.honqout.tvlauncher3.R

class IntentUtils {
    companion object {
        private const val TAG: String = "IntentUtils"

        enum class LaunchIntentResult {
            SUCCESS, URI_IS_EMPTY, NO_MATCHING_ACTIVITY
        }

        enum class LaunchActivityResult {
            SUCCESS, NOT_EXPORTED, REQUIRE_PERMISSION, NOT_FOUND
        }

        fun handleLaunchIntentResult(
            context: Context,
            result: LaunchIntentResult,
            onSuccess: () -> Unit = {},
            onFail: () -> Unit = {}
        ) {
            when (result) {
                LaunchIntentResult.SUCCESS -> {
                    onSuccess()
                }

                LaunchIntentResult.URI_IS_EMPTY -> {
                    Toast.makeText(
                        context,
                        R.string.uri_is_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                    onFail()
                }

                LaunchIntentResult.NO_MATCHING_ACTIVITY -> {
                    Toast.makeText(
                        context,
                        R.string.no_matching_activity,
                        Toast.LENGTH_SHORT
                    ).show()
                    onFail()
                }
            }
        }

        fun handleLaunchActivityResult(
            context: Context,
            result: LaunchActivityResult,
            onSuccess: () -> Unit = {},
            onFail: () -> Unit = {}
        ) {
            when (result) {
                LaunchActivityResult.SUCCESS -> {
                    onSuccess()
                }

                LaunchActivityResult.NOT_EXPORTED -> {
                    Toast.makeText(
                        context,
                        R.string.cannot_access_unexported_activity,
                        Toast.LENGTH_SHORT
                    ).show()
                    onFail()
                }

                LaunchActivityResult.REQUIRE_PERMISSION -> {
                    Toast.makeText(
                        context,
                        R.string.activity_requires_extra_permission,
                        Toast.LENGTH_SHORT
                    ).show()
                    onFail()
                }

                LaunchActivityResult.NOT_FOUND -> {
                    Toast.makeText(
                        context,
                        R.string.cannot_find_activity,
                        Toast.LENGTH_SHORT
                    ).show()
                    onFail()
                }
            }
        }

        /**
         * Launch an action which is defined by system.
         */
        fun launchAction(context: Context, action: String, newTask: Boolean): LaunchIntentResult {
            if (TextUtils.isEmpty(action)) {
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
        fun launchActivity(context: Context, intent: Intent): LaunchActivityResult {
            val activityInfo = intent.resolveActivityInfo(
                context.packageManager,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (activityInfo != null) {
                if (!activityInfo.exported) {
                    Log.e(TAG, "Cannot launch activity because it is not exported.")
                    return LaunchActivityResult.NOT_EXPORTED
                } else if (!TextUtils.isEmpty(activityInfo.permission)) {
                    Log.e(TAG, "Activity is not exported or needs extra permission to start.")
                    return LaunchActivityResult.REQUIRE_PERMISSION
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
        fun requestUninstallApp(context: Context, packageName: String): LaunchIntentResult {
            if (TextUtils.isEmpty(packageName)) {
                Log.e(TAG, "Cannot uninstall app because the given packageName is empty.")
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
                Log.e(TAG, "Cannot uninstall app because no activity can handle it.", e)
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
            if (TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot launch Settings because the given packageName is null or empty."
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
                Log.e(TAG, "Cannot launch Settings because requested package cannot be found.", e)
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }

        fun openAppInMarket(context: Context, packageName: String): LaunchIntentResult {
            if (TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot open detail page of this app in app market because the given packageName is null or empty."
                )
                return LaunchIntentResult.URI_IS_EMPTY
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setData(("market://details?id=$packageName").toUri())
            }
            try {
                context.startActivity(intent)
                return LaunchIntentResult.SUCCESS
            } catch (e: ActivityNotFoundException) {
                Log.e(
                    TAG,
                    "Cannot open detail page of this app in app market because no activity can open this uri.",
                    e
                )
                return LaunchIntentResult.NO_MATCHING_ACTIVITY
            }
        }
    }
}