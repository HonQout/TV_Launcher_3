package com.github.honqout.tvlauncher3.activity.viewmodel

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
    private var broadcastReceiver: BroadcastReceiver? = null

    init {
        registerBroadcastReceiver()
    }

    override fun onCleared() {
        unregisterBroadcastReceiver()
        super.onCleared()
    }

    fun registerBroadcastReceiver() {
        if (broadcastReceiver != null) {
            return
        }

        val context = getApplication<Application>()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
        }

        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _currentTime.value = System.currentTimeMillis()
            }
        }

        ContextCompat.registerReceiver(context, broadcastReceiver, filter, receiverFlags)
    }

    fun unregisterBroadcastReceiver() {
        broadcastReceiver?.let {
            val context = getApplication<Application>()
            context.unregisterReceiver(it)
            broadcastReceiver = null
        }
    }
}