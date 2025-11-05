package com.android.tvlauncher3.activity.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.media.tv.TvInputInfo
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tvlauncher3.R
import com.android.tvlauncher3.bean.ActivityBean
import com.android.tvlauncher3.bean.ActivityRecord
import com.android.tvlauncher3.persistence.SettingsRepository
import com.android.tvlauncher3.utils.ApplicationUtils
import com.android.tvlauncher3.utils.TvUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
    // constant
    val numFixedActivity = 5

    // persistence
    val settingsRepository = SettingsRepository(getApplication())

    // UI-related
    val tabs = listOf(
        Pair(R.drawable.baseline_home_24, R.string.home),
        Pair(R.drawable.baseline_apps_24, R.string.apps),
        Pair(R.drawable.baseline_input_24, R.string.input)
    )
    private val _topBarHeight = MutableStateFlow<Int>(0)
    val topBarHeight: StateFlow<Int> = _topBarHeight.asStateFlow()
    private val _selectedTabIndex = MutableStateFlow<Int>(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()
    private val _showSettingsPanel = MutableStateFlow<Boolean>(false)
    val showSettingsPanel: StateFlow<Boolean> = _showSettingsPanel.asStateFlow()
    private val _showAppActionDialog = MutableStateFlow<Boolean>(false)
    val showAppActionDialog: StateFlow<Boolean> = _showAppActionDialog.asStateFlow()
    private val _showAppListDialog = MutableStateFlow<Boolean>(false)
    val showAppListDialog: StateFlow<Boolean> = _showAppListDialog.asStateFlow()

    // data-related
    private val _currentTime = MutableStateFlow<Long>(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()
    private val _isInitializing = MutableStateFlow<Boolean>(false)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()
    private val _activityBeanList = mutableStateListOf<ActivityBean>()
    val activityBeanList: List<ActivityBean> = _activityBeanList
    private val _fixedActivityRecordList = mutableStateListOf<ActivityRecord?>().apply {
        addAll(List(numFixedActivity) { null })
    }
    private val _fixedActivityBeanList = mutableStateListOf<ActivityBean?>().apply {
        addAll(List(numFixedActivity) { null })
    }
    val fixedActivityBeanList: List<ActivityBean?> = _fixedActivityBeanList
    private val _focusedItemIndex1 = MutableStateFlow<Int>(0)
    val focusedItemIndex1: StateFlow<Int> = _focusedItemIndex1.asStateFlow()
    private val _focusedItemIndex2 = MutableStateFlow<Int>(0)
    val focusedItemIndex2: StateFlow<Int> = _focusedItemIndex2.asStateFlow()
    private val _focusedItemResolveInfo = MutableStateFlow<ResolveInfo?>(null)
    val focusedItemResolveInfo: StateFlow<ResolveInfo?> = _focusedItemResolveInfo.asStateFlow()
    private val _pressedItemResolveInfo = MutableStateFlow<ResolveInfo?>(null)
    val pressedItemResolveInfo: StateFlow<ResolveInfo?> = _pressedItemResolveInfo.asStateFlow()
    private val _resolveInfo = MutableStateFlow<ResolveInfo?>(null)
    val resolveInfo: StateFlow<ResolveInfo?> = _resolveInfo.asStateFlow()
    private val _tvInputList = mutableStateListOf<TvInputInfo>()
    val tvInputList: List<TvInputInfo> = _tvInputList

    // broadcastReceiver
    private var timeBroadcastReceiver: BroadcastReceiver? = null
    private var localeBroadcastReceiver: BroadcastReceiver? = null
    private var packageBroadcastReceiver: BroadcastReceiver? = null

    companion object {
        const val TAG: String = "MainViewModel"
    }

    init {
        _isInitializing.value = true
        registerTimeBR(getApplication())
        registerLocaleBR(getApplication())
        registerPackageBR(getApplication())
        loadFixedActivityList()
        loadActivityBeanList()
        loadTvInputList()
        _isInitializing.value = false
    }

    override fun onCleared() {
        viewModelScope.cancel()
        unregisterTimeBR(getApplication())
        unregisterLocaleBR(getApplication())
        unregisterPackageBR(getApplication())
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

        timeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _currentTime.value = System.currentTimeMillis()
            }
        }

        context.registerReceiver(timeBroadcastReceiver, filter)
    }

    fun unregisterTimeBR(context: Context) {
        timeBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            timeBroadcastReceiver = null
        }
    }

    fun registerLocaleBR(context: Context) {
        if (localeBroadcastReceiver != null) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }

        localeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                loadActivityBeanList()
            }
        }

        context.registerReceiver(localeBroadcastReceiver, filter)
    }

    fun unregisterLocaleBR(context: Context) {
        localeBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            localeBroadcastReceiver = null
        }
    }

    fun registerPackageBR(context: Context) {
        if (packageBroadcastReceiver != null) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        val receiverFlags = ContextCompat.RECEIVER_EXPORTED

        packageBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) {
                    Log.i(TAG, "Received message is null.")
                } else {
                    val action: String = intent.action ?: "null"
                    Log.i(TAG, "Received message: $action")
                    when (action) {
                        Intent.ACTION_PACKAGE_ADDED -> {
                            val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                            if (!isReplacing) {
                                var packageName = "null"
                                if (intent.data != null) {
                                    packageName = intent.data?.schemeSpecificPart ?: "null"
                                }
                                addItems(packageName)
                                Log.i(TAG, "Package $packageName has been added.")
                            }
                        }

                        Intent.ACTION_PACKAGE_REMOVED -> {
                            val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                            if (!isReplacing) {
                                var packageName = "null"
                                if (intent.data != null) {
                                    packageName = intent.data?.schemeSpecificPart ?: "null"
                                }
                                removeItems(packageName)
                                Log.i(TAG, "Package $packageName has been removed.")
                            }
                        }

                        Intent.ACTION_PACKAGE_REPLACED -> {
                            var packageName = "null"
                            if (intent.data != null) {
                                packageName = intent.data?.schemeSpecificPart ?: "null"
                            }
                            replaceItems(packageName)
                            Log.i(TAG, "Package $packageName has been replaced.")
                            setFocusedItemIndex2(0)
                        }

                        else -> {
                            Log.e(TAG, "Received irrelevant message.")
                        }
                    }
                }
            }
        }

        ContextCompat.registerReceiver(context, packageBroadcastReceiver, filter, receiverFlags)
    }

    fun unregisterPackageBR(context: Context) {
        packageBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            packageBroadcastReceiver = null
        }
    }

    fun loadFixedActivityList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                settingsRepository.fixedActivityRecordFlow.collect { list ->
                    _fixedActivityRecordList.clear()
                    _fixedActivityRecordList.addAll(list)
                    _fixedActivityBeanList.clear()
                    list.forEach { activityRecord ->
                        if (activityRecord == null) {
                            _fixedActivityBeanList.add(null)
                        } else {
                            val resolveInfo = ApplicationUtils.getIntentActivity(
                                getApplication(),
                                activityRecord.packageName,
                                activityRecord.activityName
                            )
                            _fixedActivityBeanList.add(
                                if (resolveInfo == null) null
                                else ActivityBean(getApplication(), resolveInfo)
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadActivityBeanList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _activityBeanList.clear()
                _activityBeanList.addAll(ApplicationUtils.getActivityBeanList(getApplication()))
                sortActivityBeanList()
            }
        }
    }

    fun loadTvInputList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _tvInputList.clear()
                _tvInputList.addAll(TvUtils.getTvInputList(getApplication()))
            }
        }
    }

    fun addItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(5000)
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
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
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
                }
            }
        }
    }

    fun removeItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from list: $removeResult")
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
                }
            }
        }
    }

    fun replaceItems(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
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
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
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

    fun updateFixedActivityList(index: Int?, item: ActivityBean?) {
        val targetIndex = index ?: _focusedItemIndex1.value
        if (targetIndex in 0..<5) {
            _fixedActivityBeanList[_focusedItemIndex1.value] = item
            _fixedActivityRecordList[_focusedItemIndex1.value] = if (item == null) {
                null
            } else {
                ActivityRecord(item.packageName, item.activityName)
            }
            viewModelScope.launch {
                settingsRepository.saveFixedActivityRecord(_fixedActivityRecordList)
            }
        }
    }

    fun setTopBarHeight(newValue: Int) {
        _topBarHeight.update {
            newValue
        }
    }

    fun setSelectedTabIndex(newValue: Int) {
        _selectedTabIndex.update {
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

    fun setShowAppListDialog(newValue: Boolean) {
        _showAppListDialog.update {
            newValue
        }
    }

    fun setFocusedItemIndex1(newValue: Int) {
        _focusedItemIndex1.update {
            newValue
        }
    }

    fun setFocusedItemIndex2(newValue: Int) {
        _focusedItemIndex2.update {
            newValue
        }
    }

    fun setFocusedItemResolveInfo(newValue: ResolveInfo?) {
        _focusedItemResolveInfo.update {
            newValue
        }
    }

    fun setPressedItemResolveInfo(newValue: ResolveInfo?) {
        _pressedItemResolveInfo.update {
            newValue
        }
    }

    fun setResolveInfo(newValue: ResolveInfo?) {
        _resolveInfo.update {
            newValue
        }
    }
}