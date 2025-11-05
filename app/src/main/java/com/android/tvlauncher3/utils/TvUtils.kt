package com.android.tvlauncher3.utils

import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.util.Log

class TvUtils {
    companion object {
        private const val TAG: String = "TvUtils"

        fun getTvInputList(context: Context): List<TvInputInfo> {
            val tvInputManager =
                context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
            return tvInputManager?.tvInputList ?: emptyList()
        }

        fun switchToTvInputSource(context: Context, tvInputInfo: TvInputInfo): Boolean {
            if (!tvInputInfo.isPassthroughInput) return false
            val uri = TvContract.buildChannelUriForPassthroughInput(tvInputInfo.id)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return runCatching {
                context.startActivity(intent)
                true
            }.getOrElse {
                Log.e(TAG, "Failed to switch to Tv input source.", it)
                false
            }
        }
    }
}