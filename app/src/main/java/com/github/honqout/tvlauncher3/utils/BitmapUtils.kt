package com.github.honqout.tvlauncher3.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette

class BitmapUtils {
    companion object {
        private const val TAG: String = "BitmapUtils"

        fun getDominantColor(bitmap: Bitmap, defaultColor: Int = Color.TRANSPARENT): Int {
            val palette = Palette.from(bitmap).generate()
            return palette.getDominantColor(defaultColor)
        }
    }
}