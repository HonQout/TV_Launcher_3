package com.github.honqout.tvlauncher3.activity.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.tv.TvInputInfo
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.honqout.tvlauncher3.utils.TvUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class InputViewModel(application: Application) : AndroidViewModel(application) {
    // UI-related
    private val _focusedItemIndex = MutableStateFlow<Int>(0)
    val focusedItemIndex: StateFlow<Int> = _focusedItemIndex.asStateFlow()

    // data-related
    private val _topBarHeight = MutableStateFlow<Int>(0)
    val topBarHeight: StateFlow<Int> = _topBarHeight.asStateFlow()
    private val _tvInputList = mutableStateListOf<TvInputInfo>()
    val tvInputList: List<TvInputInfo> = _tvInputList

    // broadcast receiver
    private var localeBroadcastReceiver: BroadcastReceiver? = null

    // mutex
    private val tvInputListMutex = Mutex()

    companion object {
        const val TAG: String = "InputViewModel"
    }

    init {
        registerLocaleBR(getApplication())
        loadTvInputList()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        unregisterLocaleBR(getApplication())
        super.onCleared()
    }

    fun registerLocaleBR(context: Context) {
        if (localeBroadcastReceiver != null) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }

        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        localeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                loadTvInputList()
            }
        }

        ContextCompat.registerReceiver(context, localeBroadcastReceiver, filter, receiverFlags)
    }

    fun unregisterLocaleBR(context: Context) {
        localeBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            localeBroadcastReceiver = null
        }
    }

    fun loadTvInputList() {
        viewModelScope.launch {
            tvInputListMutex.withLock {
                withContext(Dispatchers.Default) {
                    _tvInputList.clear()
                    _tvInputList.addAll(TvUtils.getTvInputList(getApplication()))
                }
            }
        }
    }

    fun setTopBarHeight(newValue: Int) {
        _topBarHeight.update {
            newValue
        }
    }

    fun setFocusedItemIndex3(newValue: Int) {
        _focusedItemIndex.update {
            newValue
        }
    }

    fun getSelectedTvInputInfo(): TvInputInfo? {
        return if (_focusedItemIndex.value >= 0 && _focusedItemIndex.value < _tvInputList.size) {
            _tvInputList[_focusedItemIndex.value]
        } else {
            null
        }
    }
}