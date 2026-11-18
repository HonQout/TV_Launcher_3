package com.github.honqout.tvlauncher3.utils

import android.annotation.SuppressLint
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
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette

class DrawableUtils {
    companion object {
        private const val TAG: String = "DrawableUtils"

        /**
         * Convert a drawable to bitmap.
         */
        fun toBitmap(drawable: Drawable): Bitmap {
            return when (drawable) {
                is BitmapDrawable -> drawable.bitmap
                else -> {
                    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
                    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
                    val bitmap = createBitmap(width, height)
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, width, height)
                    drawable.draw(canvas)
                    bitmap
                }
            }
        }

        /**
         * Get dominant color of a drawable.
         */
        @ColorInt
        fun getDominantColor(
            drawable: Drawable,
            defaultColor: Int = Color.TRANSPARENT
        ): Int {
            val bitmap = toBitmap(drawable)
            if (bitmap.width == 0 || bitmap.height == 0) {
                return defaultColor
            }
            val palette = Palette.from(bitmap).generate()
            return palette.getDominantColor(defaultColor)
        }

        /**
         * Get dominant color of an application icon.
         * @return The dominant color of the given `drawable`, or [Color.TRANSPARENT] if the
         * drawable does not contain any color.
         */
        @ColorInt
        fun getDominantColorOfAppIcon(
            drawable: Drawable,
            defaultColor: Int = Color.TRANSPARENT
        ): Int {
            val dominantColor = when (drawable) {
                is ColorDrawable -> {
                    drawable.color
                }

                is GradientDrawable -> {
                    getDominantColorOfGradientDrawable(drawable)
                }

                is LayerDrawable -> {
                    getDominantColorOfLayerDrawable(drawable)
                }

                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && drawable is AdaptiveIconDrawable
                    ) {
                        getDominantColorOfAdaptiveIcon(drawable)
                    } else {
                        null
                    }
                }
            }
            return if (dominantColor != null && dominantColor != Color.TRANSPARENT) {
                dominantColor
            } else {
                getDominantColor(drawable, defaultColor)
            }
        }

        /**
         * Get dominant color of a gradient drawable.
         * @return The dominant color of the given `gradientDrawable`, or [Color.TRANSPARENT] if
         * the drawable does not contain any color.
         */
        @SuppressLint("ObsoleteSdkInt")
        @ColorInt
        private fun getDominantColorOfGradientDrawable(gradientDrawable: GradientDrawable): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val color = gradientDrawable.color
                if (color != null) {
                    return color.getColorForState(
                        gradientDrawable.state,
                        color.defaultColor
                    )
                }
            }

            val colors = gradientDrawable.colors
            if (colors != null && colors.isNotEmpty()) {
                return colors[0]
            }

            return Color.TRANSPARENT
        }

        /**
         * Get dominant color of a layer drawable.
         * @return The dominant color of the given `layerDrawable`, or [Color.TRANSPARENT] if the
         * drawable does not contain any color.
         */
        private fun getDominantColorOfLayerDrawable(layerDrawable: LayerDrawable): Int {
            for (i in 0 until layerDrawable.numberOfLayers) {
                val color = when (val drawable = layerDrawable.getDrawable(i)) {
                    is ColorDrawable -> drawable.color
                    is GradientDrawable -> getDominantColorOfGradientDrawable(drawable)
                    else -> Color.TRANSPARENT
                }
                if (color != Color.TRANSPARENT) {
                    return color
                }
            }
            return Color.TRANSPARENT
        }

        /**
         * Get dominant color of an adaptive icon.
         * @return The dominant color of the given `icon`, or `null` if the icon does not contain
         * any color.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun getDominantColorOfAdaptiveIcon(icon: AdaptiveIconDrawable): Int? {
            return when (val background = icon.background) {
                is ColorDrawable -> background.color
                is GradientDrawable -> getDominantColorOfGradientDrawable(background)
                else -> null
            }
        }
    }
}