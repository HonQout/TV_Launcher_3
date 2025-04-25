package com.android.tvlauncher3.utils

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get

class ColorUtils {
    companion object {
        const val TAG: String = "ColorUtils"

        /**
         * Get the color of pixel (x,y) of Bitmap bitmap.
         * @return A ColorInt if (x,y) exists in Bitmap bitmap, -1 otherwise.
         */
        fun getColor(bitmap: Bitmap, x: Float, y: Float): Int {
            try {
                return bitmap[x.toInt(), y.toInt()]
            } catch (e: IllegalArgumentException) {
                Log.i(TAG, "Required x or y is out of range.", e)
                return -1
            } catch (e: IllegalStateException) {
                Log.i(TAG, "The config of bitmap is hardware.", e)
                return -1
            }
        }

        /**
         * Get the color of pixel (x,y) of View v.
         * @return A ColorInt if (x,y) exists in view v, -1 otherwise.
         */
        fun getColor(v: View, x: Float, y: Float): Int {
            if (x < 0 || y < 0 || x > v.width || y > v.height) {
                Log.e(TAG, "Required x or y is out of range.")
                return -1
            }
            val bitmap: Bitmap = createBitmap(v.width, v.height)
            return getColor(bitmap, x, y)
        }

        fun getLightness(color: Color): Double {
            val red = color.red
            val green = color.green
            val blue = color.blue
            return 0.299 * red + 0.587 * green + 0.114 * blue
        }

        fun isDarkColor(color: Color, threshold: Double = 0.5): Boolean {
            val lightness = getLightness(color)
            return lightness < threshold
        }

        fun isLightColor(color: Color, threshold: Double = 0.5): Boolean {
            var hsl = FloatArray(3)
            ColorUtils.RGBToHSL(color.red.toInt(), color.blue.toInt(), color.green.toInt(), hsl)
            Log.i("HSL", "HSL = ${hsl[2]}")
            return hsl[2] > threshold
        }

        fun getAppropriateTextColor(bgColor: Color): Color {
            return if (isDarkColor(bgColor, 0.6)) Color.White else Color.Black
            //return if (isLightColor(bgColor, 0.1)) Color.Black else Color.White
        }
    }
}