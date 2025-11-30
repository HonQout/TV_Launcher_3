package com.github.honqout.tvlauncher3.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.github.honqout.tvlauncher3.bean.ActivityBean

class ApplicationUtils {
    companion object {
        private const val TAG: String = "ApplicationUtils"

        enum class ApplicationType {
            UNKNOWN, SYSTEM, UPDATED_SYSTEM, USER
        }

        enum class IconType {
            Icon, Banner
        }

        fun getPackageName(resolveInfo: ResolveInfo?): String? {
            return resolveInfo?.activityInfo?.packageName
        }

        fun getPackageInfo(context: Context, packageName: String?): PackageInfo? {
            val pm: PackageManager = context.packageManager
            return if (packageName == null || TextUtils.isEmpty(packageName)) {
                Log.e(TAG, "Cannot get packageInfo because the given packageName is null or empty.")
                null
            } else {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                    } else {
                        pm.getPackageInfo(packageName, 0)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(
                        TAG,
                        "Cannot get packageInfo because package $packageName doesn't exist.",
                        e
                    )
                    null
                }
            }
        }

        fun getPackageInfo(context: Context, resolveInfo: ResolveInfo?): PackageInfo? {
            val packageName = getPackageName(resolveInfo)
            return getPackageInfo(context, packageName)
        }

        fun getApplicationIcon(context: Context, packageName: String?): Drawable {
            val pm: PackageManager = context.packageManager
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

        fun getApplicationIcon(context: Context, resolveInfo: ResolveInfo?): Drawable {
            val packageName = getPackageName(resolveInfo)
            return getApplicationIcon(context, packageName)
        }

        fun getApplicationLabel(context: Context, packageName: String): String? {
            val pm: PackageManager = context.packageManager
            val packageInfo = getPackageInfo(context, packageName)
            return packageInfo?.applicationInfo?.loadLabel(pm)?.toString()
        }

        fun getApplicationLabel(context: Context, resolveInfo: ResolveInfo?): String {
            val pm: PackageManager = context.packageManager
            val applicationInfo = resolveInfo?.activityInfo?.applicationInfo
            return applicationInfo?.loadLabel(pm).toString()
        }

        fun getActivityIcon(context: Context, resolveInfo: ResolveInfo?): Drawable {
            val pm: PackageManager = context.packageManager
            return if (resolveInfo == null) {
                pm.defaultActivityIcon
            } else {
                resolveInfo.loadIcon(pm)
            }
        }

        fun getActivityName(resolveInfo: ResolveInfo?, defaultName: String = ""): String {
            return if (resolveInfo == null) {
                defaultName
            } else {
                resolveInfo.activityInfo.name
            }
        }


        /**
         * Get the banner of the application.
         * @return Pair(PackageManager.defaultActivityIcon, IconType.Icon) if the packageName of
         * the application cannot be obtained, Pair(banner,IconType.Banner) if the application has
         * a banner, or Pair(icon or PackageManager.defaultActivityIcon,IconType.Icon) if the
         * application doesn't have a banner.
         */
        fun getApplicationBanner(
            context: Context,
            resolveInfo: ResolveInfo?
        ): Pair<Drawable, IconType> {
            val pm: PackageManager = context.packageManager
            val packageName = getPackageName(resolveInfo)
            return if (packageName == null || TextUtils.isEmpty(packageName)) {
                Pair(pm.defaultActivityIcon, IconType.Icon)
            } else {
                val banner = pm.getApplicationBanner(packageName)
                if (banner != null) {
                    Pair(banner, IconType.Banner)
                } else {
                    Pair(
                        resolveInfo?.loadIcon(pm) ?: pm.defaultActivityIcon,
                        IconType.Icon
                    )
                }
            }
        }

        fun getActivityLabel(context: Context, resolveInfo: ResolveInfo?): String {
            val pm: PackageManager = context.packageManager
            return resolveInfo?.loadLabel(pm)?.toString() ?: ""
        }

        fun getVersionName(context: Context, resolveInfo: ResolveInfo?): String? {
            val packageInfo = getPackageInfo(context, resolveInfo)
            return packageInfo?.versionName
        }

        fun getLongVersionCode(context: Context, resolveInfo: ResolveInfo?): Long? {
            val packageInfo = getPackageInfo(context, resolveInfo)
            return if (packageInfo == null) {
                null
            } else {
                PackageInfoCompat.getLongVersionCode(packageInfo)
            }
        }

        fun getVersionCode(context: Context, resolveInfo: ResolveInfo?): Int? {
            val longVersionCode = getLongVersionCode(context, resolveInfo)
            return if (longVersionCode == null) {
                null
            } else {
                (longVersionCode and 0xFFFFFFFF).toInt()
            }
        }

        fun getVersionNameAndVersionCode(context: Context, resolveInfo: ResolveInfo?): String? {
            val versionName = getVersionName(context, resolveInfo)
            val versionCode = getVersionCode(context, resolveInfo)
            return if (versionName == null || versionCode == null) {
                null
            } else {
                "$versionName ($versionCode)"
            }
        }

        /**
         * Get a list of resolveInfos of all applications installed.
         */
        fun getIntentActivityList(context: Context): List<ResolveInfo> {
            val pm: PackageManager = context.packageManager
            val intent: Intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            return pm.queryIntentActivities(intent, 0)
        }

        /**
         * Get a list of resolveInfos of the application corresponds to the given packageName.
         */
        fun getIntentActivityList(context: Context, packageName: String): List<ResolveInfo> {
            val pm: PackageManager = context.packageManager
            val intent: Intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
            }
            return pm.queryIntentActivities(intent, 0)
        }

        fun getIntentActivity(
            context: Context,
            packageName: String,
            activityName: String
        ): ResolveInfo? {
            val intentActivities = getIntentActivityList(context, packageName)
            return intentActivities.find { resolveInfo ->
                getActivityName(resolveInfo) == activityName
            }
        }

        /**
         * The implementation of function getActivityBeanList.
         */
        fun getActivityBeanList(
            context: Context,
            intentActivityList: List<ResolveInfo>
        ): List<ActivityBean> {
            val activityBeanList: MutableList<ActivityBean> = mutableListOf()
            intentActivityList.forEach { intentActivity ->
                val activityBean = ActivityBean(context, intentActivity)
                activityBeanList.add(activityBean)
            }
            return activityBeanList
        }

        /**
         * Get a list of ActivityBean of all launchable activity of all installed applications.
         */
        fun getActivityBeanList(context: Context): List<ActivityBean> {
            val intentActivityList = getIntentActivityList(context)
            return getActivityBeanList(context, intentActivityList)
        }

        /**
         * Get a list of ActivityBean of all launchable activity of the specified application.
         */
        fun getActivityBeanList(context: Context, packageName: String): List<ActivityBean> {
            val intentActivityList = getIntentActivityList(context, packageName)
            return getActivityBeanList(context, intentActivityList)
        }

        fun shouldShowBelongToHint(context: Context, resolveInfo: ResolveInfo): Boolean {
            val packageName: String = resolveInfo.activityInfo.packageName
            val applicationLabel = getApplicationLabel(context, packageName)
            val activityLabel = getActivityLabel(context, resolveInfo)
            return applicationLabel?.compareTo(activityLabel) != 0
        }

        fun getApplicationType(context: Context, resolveInfo: ResolveInfo): ApplicationType {
            val pm: PackageManager = context.packageManager
            val packageName: String = resolveInfo.activityInfo.packageName
            try {
                val packageInfo: PackageInfo =
                    pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS)
                return if ((packageInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
                    ApplicationType.UPDATED_SYSTEM
                } else if ((packageInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0) {
                    ApplicationType.SYSTEM
                } else {
                    ApplicationType.USER
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Cannot find the application with the specified packageName.", e)
                return ApplicationType.UNKNOWN
            }
        }
    }
}