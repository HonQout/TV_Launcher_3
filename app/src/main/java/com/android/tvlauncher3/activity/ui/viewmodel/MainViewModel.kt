package com.android.tvlauncher3.activity.ui.viewmodel

import android.app.Application
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
    private val _showAppActionDialog = MutableStateFlow<Boolean>(false)
    val showAppActionDialog: StateFlow<Boolean> = _showAppActionDialog.asStateFlow()

    // data-related
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _activityBeanList = mutableStateListOf<ActivityBean>()
    val activityBeanList: List<ActivityBean> = _activityBeanList
    private val _lastFocusedColumn = MutableStateFlow<Int>(0)
    val lastFocusedColumn: StateFlow<Int> = _lastFocusedColumn
    private val _focusedItemIndex = MutableStateFlow<Int>(0)
    val focusedItemIndex: StateFlow<Int> = _focusedItemIndex.asStateFlow()
    private val _selectedResolveInfo = MutableStateFlow<ResolveInfo?>(null)
    val selectedResolveInfo: StateFlow<ResolveInfo?> = _selectedResolveInfo.asStateFlow()

    companion object {
        const val TAG: String = "MainViewModel"
    }

    init {
        loadActivityBeanList()
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
                val application = getApplication<Application>()
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                Log.i(TAG, "Added items to list: $addResult")
                Log.i(TAG, "New size of resolve info list is ${_activityBeanList.size}.")
                sortActivityBeanList()
            }
        }
    }

    fun removeItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
            }
        }
    }

    fun replaceItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val application = getApplication<Application>()
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                Log.i(TAG, "Added items to list: $addResult")
                Log.i(TAG, "New size of resolve info list is ${_activityBeanList.size}.")
                sortActivityBeanList()
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

    fun setLastFocusedColumn(newValue: Int) {
        _lastFocusedColumn.update {
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

    fun setShowAppActionDialog(newValue: Boolean) {
        _showAppActionDialog.update {
            newValue
        }
    }
}