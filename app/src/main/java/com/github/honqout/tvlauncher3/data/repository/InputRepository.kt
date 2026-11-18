package com.github.honqout.tvlauncher3.data.repository

import android.content.Context
import android.media.tv.TvInputManager
import android.os.Handler
import android.os.Looper

class InputRepository {
    companion object {
        private const val TAG: String = "InputRepository"
    }

    private var tvInputManager: TvInputManager?
    private var callback: TvInputManager.TvInputCallback
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    constructor(context: Context, callback: TvInputManager.TvInputCallback) {
        val appContext = context.applicationContext
        tvInputManager = appContext.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        this.callback = callback
    }

    fun register(): Boolean {
        return if (tvInputManager != null) {
            tvInputManager?.registerCallback(callback, mainHandler)
            true
        } else {
            false
        }
    }

    fun unregister(): Boolean {
        return if (tvInputManager != null) {
            tvInputManager?.unregisterCallback(callback)
            true
        } else {
            false
        }
    }
}