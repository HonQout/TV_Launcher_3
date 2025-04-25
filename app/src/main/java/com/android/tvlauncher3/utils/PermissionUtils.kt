package com.android.tvlauncher3.utils

import android.app.Activity
import android.content.pm.PackageManager

class PermissionUtils {
    companion object {
        fun checkSelfPermission(activity: Activity, permission: String): Boolean {
            return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

        fun checkSelfPermissions(activity: Activity, permissions: Array<String>): Boolean {
            var granted = true
            for (permission in permissions) {
                if (!checkSelfPermission(activity, permission)) {
                    granted = false
                    break
                }
            }
            return granted
        }
    }
}