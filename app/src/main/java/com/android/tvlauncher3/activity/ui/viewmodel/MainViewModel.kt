package com.android.tvlauncher3.activity.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tvlauncher3.bean.ActivityBean
import com.android.tvlauncher3.utils.ApplicationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // UI-related
    private val _topBarHeight = MutableStateFlow<Int>(0)
    val topBarHeight: StateFlow<Int> = _topBarHeight.asStateFlow()
    private val _showSettingsPanel = MutableStateFlow<Boolean>(false)
    val showSettingsPanel: StateFlow<Boolean> = _showSettingsPanel.asStateFlow()
    private val _showAppActionDialog = MutableStateFlow<Boolean>(false)
    val showAppActionDialog: StateFlow<Boolean> = _showAppActionDialog.asStateFlow()

    // data-related
    private val _currentTime = MutableStateFlow<Long>(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _activityBeanList = mutableStateListOf<ActivityBean>()
    val activityBeanList: List<ActivityBean> = _activityBeanList
    private val _focusedItemIndex = MutableStateFlow<Int>(0)
    val focusedItemIndex: StateFlow<Int> = _focusedItemIndex.asStateFlow()
    private val _selectedResolveInfo = MutableStateFlow<ResolveInfo?>(null)
    val selectedResolveInfo: StateFlow<ResolveInfo?> = _selectedResolveInfo.asStateFlow()

    // broadcastReceiver
    private var timeBroadcastReceiver: BroadcastReceiver? = null

    companion object {
        const val TAG: String = "MainViewModel"
    }

    init {
        registerTimeUpdate(getApplication<Application>())
        loadActivityBeanList()
    }

    override fun onCleared() {
        super.onCleared()
        unregisterTimeUpdate(getApplication<Application>())
    }

    fun registerTimeUpdate(context: Context) {
        if (timeBroadcastReceiver != null) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
        }

        timeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _currentTime.value = System.currentTimeMillis()
            }
        }

        context.registerReceiver(timeBroadcastReceiver, filter)
    }

    fun unregisterTimeUpdate(context: Context) {
        timeBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            timeBroadcastReceiver = null
        }
    }

    fun loadActivityBeanList() {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                _activityBeanList.clear()
                _activityBeanList.addAll(ApplicationUtils.getActivityBeanList(getApplication()))
                sortActivityBeanList()
            }
            _isLoading.value = false
        }
    }

    fun addItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(5000)
                val focusedItem =
                    if (_focusedItemIndex.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val application = getApplication<Application>()
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                Log.i(TAG, "Added items to list: $addResult")
                Log.i(TAG, "New size of resolve info list is ${_activityBeanList.size}.")
                sortActivityBeanList()
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex(currentIndex)
                } else {
                    setFocusedItemIndex(0)
                }
            }
        }
    }

    fun removeItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val focusedItem =
                    if (_focusedItemIndex.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex(currentIndex)
                } else {
                    setFocusedItemIndex(0)
                }
            }
        }
    }

    fun replaceItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val focusedItem =
                    if (_focusedItemIndex.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val application = getApplication<Application>()
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                Log.i(TAG, "Added items to list: $addResult")
                Log.i(TAG, "New size of resolve info list is ${_activityBeanList.size}.")
                sortActivityBeanList()
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex(currentIndex)
                } else {
                    setFocusedItemIndex(0)
                }
            }
        }
    }

    fun sortActivityBeanList(
        locale: Locale = Locale.CHINA
    ) {
        val collator: Collator = Collator.getInstance(locale)
        _activityBeanList.sortWith { a, b ->
            collator.compare(
                a.label,
                b.label
            )
        }
    }

    fun setTopBarHeight(newValue: Int) {
        _topBarHeight.update {
            newValue
        }
    }

    fun setShowSettingsPanel(newValue: Boolean) {
        _showSettingsPanel.update {
            newValue
        }
    }

    fun setShowAppActionDialog(newValue: Boolean) {
        _showAppActionDialog.update {
            newValue
        }
    }

    fun setFocusedItemIndex(newValue: Int) {
        _focusedItemIndex.update {
            newValue
        }
    }

    fun setSelectedResolveInfo(newValue: ResolveInfo?) {
        _selectedResolveInfo.update {
            newValue
        }
    }
}