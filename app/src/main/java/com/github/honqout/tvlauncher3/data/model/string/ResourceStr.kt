package com.github.honqout.tvlauncher3.data.model.string

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class ResourceStr {
    data class Str(
        val value: String
    ) : ResourceStr()

    class StrFromResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : ResourceStr()

    fun asString(context: Context): String {
        return when (this) {
            is Str -> value
            is StrFromResource -> context.getString(resId, *args)
        }
    }

    @Composable
    fun asComposableString(): String {
        return when (this) {
            is Str -> value
            is StrFromResource -> stringResource(resId, *args)
        }
    }
}