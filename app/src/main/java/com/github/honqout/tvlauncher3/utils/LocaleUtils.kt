package com.github.honqout.tvlauncher3.utils

import android.content.Context
import java.util.Locale

class LocaleUtils {
    companion object {
        fun getPrimaryLocale(context: Context?, default: Locale = Locale.getDefault()): Locale {
            val locale = context?.resources?.configuration?.locales[0]
            return locale ?: default
        }
    }
}