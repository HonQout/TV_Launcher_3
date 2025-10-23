package com.android.tvlauncher3.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

        fun getDominantColor(drawable: Drawable, defaultColor: Int = Color.TRANSPARENT): Int {
            val bitmap = toBitmap(drawable)
            if (bitmap == null) {
                Log.e(TAG, "BitmapDrawable does not contain a valid bitmap.")
                return defaultColor
            }
            val palette = Palette.from(bitmap).generate()
            return palette.getDominantColor(defaultColor)
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

        fun getBackgroundColorFromAppIcon(
            drawable: Drawable,
            defaultColor: Int = Color.TRANSPARENT
        ): Int {
            val backgroundColor = when (drawable) {
                is LayerDrawable -> {
                    getBackgroundColorFromLayerDrawable(drawable)
                }

                is ColorDrawable -> {
                    drawable.color
                }

                is GradientDrawable -> {
                    getColorFromGradientDrawable(drawable)
                }

                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
                        getBackgroundColorFromAdaptiveIcon(drawable)
                    } else {
                        getDominantColor(drawable, defaultColor)
                    }
                }
            }
            return if (backgroundColor == null || backgroundColor == Color.TRANSPARENT) {
                getDominantColor(drawable, defaultColor)
            } else {
                backgroundColor
            }
        }

        private fun getBackgroundColorFromLayerDrawable(layerDrawable: LayerDrawable): Int? {
            for (i in 0 until layerDrawable.numberOfLayers) {
                val drawable = layerDrawable.getDrawable(i)
                val color = when (drawable) {
                    is ColorDrawable -> drawable.color
                    is GradientDrawable -> getColorFromGradientDrawable(drawable)
                    else -> null
                }
                if (color != null) {
                    return color
                }
            }
            return null
        }

        private fun getColorFromGradientDrawable(gradientDrawable: GradientDrawable): Int? {
            val colors = gradientDrawable.colors
            return if (colors != null && colors.size == 1) {
                colors[0]
            } else {
                gradientDrawable.color?.defaultColor
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getBackgroundColorFromAdaptiveIcon(icon: AdaptiveIconDrawable): Int? {
            val background = icon.background
            return when (background) {
                is ColorDrawable -> background.color
                is GradientDrawable -> getColorFromGradientDrawable(background)
                else -> getDominantColor(background)
            }
        }
    }
}