package com.android.tvlauncher3.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette

class DrawableUtils {
    companion object {
        private const val TAG: String = "DrawableUtils"

        fun toBitmap(drawable: Drawable): Bitmap? {
            if (drawable is BitmapDrawable) {
                val bitmapDrawable: BitmapDrawable = drawable
                return if (bitmapDrawable.bitmap != null) {
                    bitmapDrawable.bitmap
                } else {
                    null
                }
            } else {
                val intrinsicWidth = drawable.intrinsicWidth
                val intrinsicHeight = drawable.intrinsicHeight
                val width = if (intrinsicWidth <= 0) {
                    1
                } else {
                    intrinsicWidth
                }
                val height = if (intrinsicHeight <= 0) {
                    1
                } else {
                    intrinsicHeight
                }
                val bitmap = createBitmap(width, height)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return bitmap
            }
        }

        fun getDominantColor(drawable: Drawable): Int {
            val bitmap = toBitmap(drawable)
            if (bitmap == null) {
                Log.e(TAG, "BitmapDrawable does not contain a valid bitmap.")
                return Color.TRANSPARENT
            }
            val palette = Palette.from(bitmap).generate()
            return palette.getDominantColor(Color.TRANSPARENT)
        }

        fun Drawable.toImageBitmap(): ImageBitmap {
            val bitmap = when (this) {
                is BitmapDrawable -> bitmap
                else -> {
                    val width = intrinsicWidth.coerceAtLeast(1)
                    val height = intrinsicHeight.coerceAtLeast(1)
                    createBitmap(width, height)
                }
            }
            return bitmap.asImageBitmap()
        }
    }
}