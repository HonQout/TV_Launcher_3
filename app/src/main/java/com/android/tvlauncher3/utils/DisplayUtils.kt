package com.android.tvlauncher3.utils

import android.content.Context
import android.util.DisplayMetrics

class DisplayUtils {
    companion object {
        const val TAG: String = "DisplayUtils"

        fun getDpi(context: Context): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            return displayMetrics.densityDpi
        }

        fun pixelToDp(context: Context, pixel: Int): Int {
            val dpi = getDpi(context)
            return 160 * pixel / dpi
        }
    }
}