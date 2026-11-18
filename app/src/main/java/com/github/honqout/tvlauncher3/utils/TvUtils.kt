package com.github.honqout.tvlauncher3.utils

import android.content.ActivityNotFoundException
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
            val appContext = context.applicationContext
            val tvInputManager =
                appContext.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
            return tvInputManager?.tvInputList ?: emptyList()
        }

        /**
         * Open the TV application and display the screen of the given [tvInputInfo].
         */
        fun switchToTvInputSource(context: Context, tvInputInfo: TvInputInfo): Boolean {
            if (!tvInputInfo.isPassthroughInput) {
                Log.w(TAG, "switchToTvInputSource: Input is not a passthrough input.")
                return false
            }

            val uri = TvContract.buildChannelUriForPassthroughInput(tvInputInfo.id)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return try {
                context.startActivity(intent)
                true
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Failed to switch to TV input source.", e)
                false
            }
        }
    }
}