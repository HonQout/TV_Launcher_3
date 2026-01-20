package com.github.honqout.tvlauncher3.activity.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimeViewModel(application: Application) : AndroidViewModel(application) {
    // data
    private val _currentTime = MutableStateFlow<Long>(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    // broadcast receiver
    private var timeBroadcastReceiver: BroadcastReceiver? = null

    init {
        registerTimeBR(getApplication())
    }

    override fun onCleared() {
        unregisterTimeBR(getApplication())
        super.onCleared()
    }

    fun registerTimeBR(context: Context) {
        if (timeBroadcastReceiver != null) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
        }

        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        timeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _currentTime.value = System.currentTimeMillis()
            }
        }

        ContextCompat.registerReceiver(context, timeBroadcastReceiver, filter, receiverFlags)
    }

    fun unregisterTimeBR(context: Context) {
        timeBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            timeBroadcastReceiver = null
        }
    }
}