package com.github.honqout.tvlauncher3.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.DeprecatedSinceApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {
    companion object {
        private const val TAG: String = "PermissionUtils"

        /**
         * Check if a permission is granted.
         */
        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Check if a group of permissions is granted.
         */
        fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (!isPermissionGranted(context, permission)) {
                    return false
                }
            }
            return true
        }

        /**
         * Get the denied permissions in a group of permissions.
         */
        fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
            val permissionDenied = mutableListOf<String>()
            for (permission in permissions) {
                if (!isPermissionGranted(context, permission)) {
                    permissionDenied.add(permission)
                }
            }
            return permissionDenied
        }

        /**
         * Check if storage permission is granted. That is to say, check if
         * [Manifest.permission.READ_EXTERNAL_STORAGE] and
         * [Manifest.permission.WRITE_EXTERNAL_STORAGE] are granted on devices running Android 12
         * or below. On devices running Android 13 or above, these two permissions are deprecated.
         * Developers should check if the permission to access a certain kind of files (including
         * audio, image and video) is granted according to their requirements.
         */
        fun isStoragePermissionGranted(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.i(
                    TAG,
                    "Needless to request permission to access storage. Request permission " +
                            "to read audio, image or video instead."
                )
                return false
            } else {
                return arePermissionsGranted(
                    context, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }

        /**
         * Request for storage permission. That is to say, request for
         * [Manifest.permission.READ_EXTERNAL_STORAGE] and
         * [Manifest.permission.WRITE_EXTERNAL_STORAGE] on devices running Android 12 or below.
         * On devices running Android 13 or above, these two permissions are deprecated. Developers
         * should request the permission to access a certain kind of files (including audio, image
         * and video) according to their requirements.
         */
        @DeprecatedSinceApi(api = Build.VERSION_CODES.TIRAMISU)
        fun requestStoragePermission(activity: Activity, requestCode: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.i(
                    TAG,
                    "Needless to request permission to access storage. Request permission " +
                            "to read audio, image or video instead."
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    requestCode
                )
            }
        }

        /**
         * Check if permission to query all packages is granted. That is to say, check if
         * [Manifest.permission.QUERY_ALL_PACKAGES] is granted on devices running Android 11 or
         * above, or simply return true on devices running Android 10 or below, for the reason that
         * not until Android 11 did Google started to restrict access to package list.
         */
        fun checkQueryAllPackagesPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return isPermissionGranted(context, Manifest.permission.QUERY_ALL_PACKAGES)
            } else {
                Log.i(TAG, "Needless to check if permission to query all packages is granted.")
                return true
            }
        }

        /**
         * Request for permission to query all packages. That is to say, request for
         * [Manifest.permission.QUERY_ALL_PACKAGES] on devices which run Android 11 or above, or
         * do nothing on devices which run Android 10 or below, for the reason that not until
         * Android 11 did Google started to restrict access to package list.
         */
        fun requestQueryAllPackagesPermission(activity: Activity, requestCode: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf<String>(Manifest.permission.QUERY_ALL_PACKAGES),
                    requestCode
                )
            } else {
                Log.i(TAG, "Needless to request permission to query all packages.")
            }
        }
    }
}