package com.github.honqout.tvlauncher3.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.pm.PackageInfoCompat
import com.github.honqout.tvlauncher3.bean.ActivityBean

class ApplicationUtils {
    companion object {
        private const val TAG: String = "ApplicationUtils"

        enum class ApplicationType {
            UNKNOWN, SYSTEM, UPDATED_SYSTEM, USER
        }

        enum class LauncherActivityType {
            NORMAL, LEANBACK
        }

        enum class IconType {
            Icon, Banner
        }

        fun getPackageName(resolveInfo: ResolveInfo?): String {
            return resolveInfo?.activityInfo?.packageName ?: ""
        }

        fun getPackageInfo(context: Context, packageName: String?): PackageInfo? {
            if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(TAG, "Cannot get PackageInfo because the given packageName is null or empty.")
                return null
            }
            val pm = context.packageManager
            try {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    pm.getPackageInfo(packageName, 0)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(
                    TAG,
                    "Cannot get PackageInfo because package $packageName doesn't exist.",
                    e
                )
                return null
            }
        }

        fun getApplicationInfo(context: Context, packageName: String?): ApplicationInfo? {
            if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot get ApplicationInfo because the given packageName is null or empty."
                )
                return null
            }
            val pm = context.packageManager
            try {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getApplicationInfo(
                        packageName,
                        PackageManager.ApplicationInfoFlags.of(0)
                    )
                } else {
                    pm.getApplicationInfo(packageName, 0)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(
                    TAG,
                    "Cannot get ApplicationInfo because package $packageName doesn't exist.",
                    e
                )
                return null
            }
        }

        fun getApplicationIconType(context: Context, packageName: String?): IconType {
            if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot get type of application icon because the given packageName is null or empty."
                )
                return IconType.Icon
            }
            val pm = context.packageManager
            try {
                val banner = pm.getApplicationBanner(packageName)
                return if (banner == null) {
                    IconType.Icon
                } else {
                    IconType.Banner
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(
                    TAG,
                    "Cannot get type of application icon because package $packageName doesn't exist.",
                    e
                )
                return IconType.Icon
            }
        }

        fun getApplicationIcon(context: Context, packageName: String?): Drawable {
            val pm = context.packageManager
            return if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot get application icon because the given packageName is null or empty."
                )
                pm.defaultActivityIcon
            } else {
                try {
                    pm.getApplicationIcon(packageName)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(
                        TAG,
                        "Cannot get application icon because package $packageName doesn't exist.",
                        e
                    )
                    pm.defaultActivityIcon
                }
            }
        }

        @DrawableRes
        fun getApplicationIconId(context: Context, packageName: String?): Int {
            val applicationInfo = getApplicationInfo(context, packageName)
            if (applicationInfo != null) {
                return applicationInfo.icon
            }
            return 0
        }

        fun getApplicationBanner(context: Context, packageName: String?): Drawable {
            val pm = context.packageManager
            return if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(
                    TAG,
                    "Cannot get application icon because the given packageName is null or empty."
                )
                pm.defaultActivityIcon
            } else {
                try {
                    pm.getApplicationBanner(packageName) ?: pm.defaultActivityIcon
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(
                        TAG,
                        "Cannot get application icon because package $packageName doesn't exist.",
                        e
                    )
                    pm.defaultActivityIcon
                }
            }
        }

        @DrawableRes
        fun getApplicationBannerId(context: Context, packageName: String?): Int {
            val applicationInfo = getApplicationInfo(context, packageName)
            if (applicationInfo != null) {
                return applicationInfo.banner
            }
            return 0
        }

        /**
         * Get Pair(IconType, (Drawable) Icon) of the application.
         * @return Pair(IconType.Icon, PackageManager.defaultActivityIcon) if the packageName of
         * the application cannot be obtained, Pair(IconType.Banner, banner) if the application has
         * a banner, or Pair(IconType.Icon, icon or PackageManager.defaultActivityIcon) if the
         * application doesn't have a banner.
         */
        fun getApplicationIconPair(
            context: Context,
            packageName: String?
        ): Pair<IconType, Drawable> {
            val pm = context.packageManager
            return if (packageName == null || TextUtils.isEmpty(packageName)) {
                Pair(IconType.Icon, pm.defaultActivityIcon)
            } else {
                val banner = pm.getApplicationBanner(packageName)
                if (banner != null) {
                    Pair(IconType.Banner, banner)
                } else {
                    Pair(IconType.Icon, getApplicationIcon(context, packageName))
                }
            }
        }

        fun getApplicationLabel(context: Context, packageName: String): String {
            val pm = context.packageManager
            val applicationInfo = getApplicationInfo(context, packageName)
            return applicationInfo?.loadLabel(pm)?.toString() ?: ""
        }

        fun getApplicationType(context: Context, packageName: String?): ApplicationType {
            val applicationInfo = getApplicationInfo(context, packageName)
            if (applicationInfo != null) {
                val flags = applicationInfo.flags
                return if (flags.and(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    ApplicationType.UPDATED_SYSTEM
                } else if (flags.and(ApplicationInfo.FLAG_SYSTEM) != 0) {
                    ApplicationType.SYSTEM
                } else {
                    ApplicationType.USER
                }
            }
            return ApplicationType.UNKNOWN
        }

        fun getShortcuts(context: Context, packageName: String?): List<ShortcutInfo?>? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val launcherApps =
                    context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                try {
                    if (launcherApps != null && launcherApps.hasShortcutHostPermission()) {
                        val query = LauncherApps.ShortcutQuery().apply {
                            setPackage(packageName)
                            setQueryFlags(
                                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
                                        or LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
                                        or LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
                            )
                        }
                        return launcherApps.getShortcuts(query, Process.myUserHandle())
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Cannot get shortcuts of package $packageName.", e)
                }
            }
            return emptyList()
        }

        fun launchAppShortcut(context: Context, packageName: String, shortcutId: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val launcherApps =
                    context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                try {
                    if (launcherApps != null && launcherApps.hasShortcutHostPermission()) {
                        launcherApps.startShortcut(
                            packageName,
                            shortcutId,
                            null,
                            null,
                            Process.myUserHandle()
                        )
                        return true
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Cannot launch shortcut $shortcutId of package $packageName.", e)
                }
            }
            return false
        }

        fun getActivityInfo(
            context: Context,
            packageName: String?,
            activityName: String?
        ): ActivityInfo? {
            val pm = context.packageManager
            if (packageName != null && activityName != null) {
                try {
                    return pm.getActivityInfo(ComponentName(packageName, activityName), 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(
                        TAG,
                        "Cannot get ActivityInfo because activity $activityName of package $packageName doesn't exist.",
                        e
                    )
                }
            }
            return null
        }

        fun getActivityIcon(
            context: Context,
            packageName: String?,
            activityName: String?
        ): Drawable {
            val pm = context.packageManager
            if (packageName != null && activityName != null) {
                try {
                    return pm.getActivityIcon(ComponentName(packageName, activityName))
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(
                        TAG,
                        "Cannot get activity icon because activity $activityName of package $packageName doesn't exist.",
                        e
                    )
                }
            }
            return pm.defaultActivityIcon
        }

        @DrawableRes
        fun getActivityIconId(context: Context, packageName: String?, activityName: String?): Int {
            if (packageName != null && activityName != null) {
                val activityInfo = getActivityInfo(context, packageName, activityName)
                return getActivityIconId(activityInfo)
            }
            return 0
        }

        @DrawableRes
        fun getActivityIconId(activityInfo: ActivityInfo?): Int {
            if (activityInfo != null) {
                return activityInfo.icon
            }
            return 0
        }

        /**
         * Get Pair(IconType, (Drawable) Icon) of a concrete activity of the application.
         * @return Pair(IconType.Icon, PackageManager.defaultActivityIcon) if the packageName of
         * the application cannot be obtained, Pair(IconType.Banner, banner) if the application has
         * a banner, or Pair(IconType.Icon, icon or PackageManager.defaultActivityIcon) if the
         * application doesn't have a banner.
         */
        fun getActivityIconPair(
            context: Context,
            packageName: String?,
            activityName: String?
        ): Pair<IconType, Drawable> {
            val pm = context.packageManager
            return if ((packageName == null || TextUtils.isEmpty(packageName))
                || (activityName == null || TextUtils.isEmpty(activityName))
            ) {
                Pair(IconType.Icon, pm.defaultActivityIcon)
            } else {
                val banner = pm.getApplicationBanner(packageName)
                if (banner != null) {
                    Pair(IconType.Banner, banner)
                } else {
                    Pair(
                        IconType.Icon,
                        getActivityIcon(context, packageName, activityName)
                    )
                }
            }
        }

        fun getActivityName(resolveInfo: ResolveInfo?): String {
            return resolveInfo?.activityInfo?.name ?: ""
        }

        fun getActivityLabel(
            context: Context,
            packageName: String?,
            activityName: String?
        ): String {
            val pm = context.packageManager
            val activityInfo = getActivityInfo(context, packageName, activityName)
            return activityInfo?.loadLabel(pm)?.toString() ?: ""
        }

        fun getActivityLabel(context: Context, resolveInfo: ResolveInfo?): String {
            val pm = context.packageManager
            return resolveInfo?.activityInfo?.loadLabel(pm).toString()
        }

        fun getVersionName(context: Context, packageName: String?): String? {
            return getPackageInfo(context, packageName)?.versionName
        }

        fun getLongVersionCode(context: Context, packageName: String?): Long? {
            val packageInfo = getPackageInfo(context, packageName)
            return if (packageInfo == null) {
                null
            } else {
                PackageInfoCompat.getLongVersionCode(packageInfo)
            }
        }

        fun getVersionCode(context: Context, packageName: String?): Int? {
            val longVersionCode = getLongVersionCode(context, packageName)
            return if (longVersionCode == null) {
                null
            } else {
                (longVersionCode and 0xFFFFFFFF).toInt()
            }
        }

        fun getVersionNameAndVersionCode(context: Context, packageName: String): String {
            val versionName = getVersionName(context, packageName)
            val versionCode = getVersionCode(context, packageName)
            return if (versionName == null || versionCode == null) {
                ""
            } else {
                "$versionName ($versionCode)"
            }
        }

        /**
         * Get a list of ResolveInfo of launcher activity.
         *
         * @param packageName Specify which package should these activities belong to. Passing
         *                    null or empty string ("") to get all activities of all installed
         *                    packages.
         */
        fun getLauncherActivityList(
            context: Context,
            type: LauncherActivityType,
            packageName: String?
        ): List<ResolveInfo> {
            val pm: PackageManager = context.packageManager
            val intent: Intent = Intent(Intent.ACTION_MAIN).apply {
                when (type) {
                    LauncherActivityType.NORMAL -> {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    }

                    LauncherActivityType.LEANBACK -> {
                        addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                    }
                }

                if (packageName != null && !TextUtils.isEmpty(packageName)) {
                    setPackage(packageName)
                }
            }
            return pm.queryIntentActivities(intent, 0)
        }

        fun getLauncherActivity(
            context: Context,
            type: LauncherActivityType,
            packageName: String,
            activityName: String
        ): ResolveInfo? {
            val intentActivities = getLauncherActivityList(context, type, packageName)
            return intentActivities.find { resolveInfo ->
                getActivityName(resolveInfo) == activityName
            }
        }

        /**
         * Get a list of ActivityBean of all launchable activity of certain application(s).
         *
         * @param packageName Specify which package should these ActivityBeans belong to. Passing
         *                    null or empty string ("") to get all ActivityBeans of all installed
         *                    packages.
         */
        fun getActivityBeanList(
            context: Context,
            type: LauncherActivityType,
            packageName: String?
        ): List<ActivityBean> {
            val intentActivityList = getLauncherActivityList(context, type, packageName)
            val activityBeanList: MutableList<ActivityBean> = mutableListOf()
            intentActivityList.forEach { intentActivity ->
                val activityBean = ActivityBean(context, intentActivity)
                activityBeanList.add(activityBean)
            }
            return activityBeanList
        }

        fun shouldShowBelongToHint(
            context: Context,
            packageName: String?,
            activityName: String?
        ): Boolean {
            if (packageName == null || activityName == null) {
                return false
            }
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(activityName)) {
                return false
            }
            val applicationLabel = getApplicationLabel(context, packageName)
            val activityLabel = getActivityLabel(context, packageName, activityName)
            return applicationLabel.compareTo(activityLabel) != 0
        }
    }
}