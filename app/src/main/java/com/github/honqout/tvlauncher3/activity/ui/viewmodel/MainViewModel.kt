package com.github.honqout.tvlauncher3.activity.ui.viewmodel

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
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.bean.ActivityBean
import com.github.honqout.tvlauncher3.bean.ActivityRecord
import com.github.honqout.tvlauncher3.persistence.SettingsRepository
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.LocaleUtils
import com.github.honqout.tvlauncher3.utils.TvUtils
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

    // system-provided
    private val _currentTime = MutableStateFlow<Long>(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()
    private val _currentLocale = MutableStateFlow<Locale>(Locale.getDefault())

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
    private val _isInitializing = MutableStateFlow<Boolean>(false)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()
    private val _activityBeanList = mutableStateListOf<ActivityBean>()
    val activityBeanList: List<ActivityBean> = _activityBeanList
    private val _fixedActivityRecordList = mutableStateListOf<ActivityRecord?>()
    private val _fixedActivityBeanList = mutableStateListOf<ActivityBean?>()
    val fixedActivityBeanList: List<ActivityBean?> = _fixedActivityBeanList
    private val _focusedItemIndex1 = MutableStateFlow<Int>(0)
    val focusedItemIndex1: StateFlow<Int> = _focusedItemIndex1.asStateFlow()
    private val _focusedItemIndex2 = MutableStateFlow<Int>(0)
    val focusedItemIndex2: StateFlow<Int> = _focusedItemIndex2.asStateFlow()
    private val _focusedItemIndex3 = MutableStateFlow<Int>(0)
    val focusedItemIndex3: StateFlow<Int> = _focusedItemIndex3.asStateFlow()
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
        _currentLocale.value = LocaleUtils.getPrimaryLocale(getApplication())
        loadFixedActivityList()
        loadActivityBeanList(_currentLocale.value)
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
                _currentLocale.value = LocaleUtils.getPrimaryLocale(context)
                loadFixedActivityList()
                loadActivityBeanList(_currentLocale.value)
                loadTvInputList()
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
                    Log.i(TAG, "Received intent action: $action")
                    when (action) {
                        Intent.ACTION_PACKAGE_ADDED -> {
                            val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                            Log.i(TAG, "Extra: replacing = $replacing")
                            if (!replacing) {
                                val packageName = intent.data?.schemeSpecificPart ?: ""
                                if (packageName != "") {
                                    addActivityBeans(packageName)
                                }
                            }
                        }

                        Intent.ACTION_PACKAGE_REMOVED -> {
                            val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                            val dataRemoved =
                                intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false)
                            Log.i(TAG, "Extra: replacing = $replacing, data_removed = $dataRemoved")
                            if (!replacing) {
                                val packageName = intent.data?.schemeSpecificPart ?: ""
                                removeActivityBeans(packageName)
                                if (packageName != "") {
                                    removeActivityBeans(packageName)
                                }
                            }
                        }

                        Intent.ACTION_PACKAGE_REPLACED -> {
                            val packageName = intent.data?.schemeSpecificPart ?: ""
                            if (packageName != "") {
                                replaceActivityBeans(packageName)
                                setFocusedItemIndex2(0)
                            }
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
            settingsRepository.fixedActivityRecordFlow.collect { list ->
                _fixedActivityRecordList.clear()
                _fixedActivityBeanList.clear()
                if (list.size != numFixedActivity) {
                    _fixedActivityRecordList.addAll(List(5) { null })
                    _fixedActivityBeanList.addAll(List(5) { null })
                    updateFixedActivityList(0, null)
                } else {
                    _fixedActivityRecordList.addAll(list)
                    list.forEach { activityRecord ->
                        _fixedActivityBeanList.add(
                            if (activityRecord == null) {
                                null
                            } else {
                                val resolveInfo = ApplicationUtils.getIntentActivity(
                                    getApplication(),
                                    activityRecord.packageName,
                                    activityRecord.activityName
                                )
                                if (resolveInfo == null) {
                                    null
                                } else {
                                    ActivityBean(getApplication(), resolveInfo)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    fun loadActivityBeanList(locale: Locale = Locale.getDefault()) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _activityBeanList.clear()
                _activityBeanList.addAll(ApplicationUtils.getActivityBeanList(getApplication()))
                sortActivityBeanList(locale)
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

    fun updateFixedActivities(context: Context, packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                for (i in 0..<numFixedActivity) {
                    val oldActivityBean = _fixedActivityBeanList[i]
                    if (oldActivityBean != null) {
                        if (packageName == oldActivityBean.packageName) {
                            val newResolveInfo = ApplicationUtils.getIntentActivity(
                                context,
                                oldActivityBean.packageName,
                                oldActivityBean.activityName
                            )
                            if (newResolveInfo == null) {
                                _fixedActivityBeanList[i] = null
                                _fixedActivityRecordList[i] = null
                            } else {
                                val newActivityBean = ActivityBean(context, newResolveInfo)
                                val newActivityRecord = ActivityRecord(
                                    newActivityBean.packageName,
                                    newActivityBean.activityName
                                )
                                _fixedActivityBeanList[i] = newActivityBean
                                _fixedActivityRecordList[i] = newActivityRecord
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                settingsRepository.saveFixedActivityRecord(_fixedActivityRecordList)
            }
        }
    }

    fun addActivityBeans(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                delay(1000)
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from resolveInfoList: $removeResult")
                val application = getApplication<Application>()
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                sortActivityBeanList()
                Log.i(TAG, "Added items to resolveInfoList: $addResult")
                Log.i(TAG, "New size of resolveInfoList is ${_activityBeanList.size}.")
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
                }
            }
        }
    }

    fun removeActivityBeans(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from resolveInfoList: $removeResult")
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
                }
            }
        }
        updateFixedActivities(getApplication(), packageName)
    }

    fun replaceActivityBeans(packageName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val focusedItem =
                    if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                        _activityBeanList[_focusedItemIndex2.value]
                    } else {
                        _activityBeanList[0]
                    }
                val removeResult = _activityBeanList.removeAll { activityBean ->
                    activityBean.packageName == packageName
                }
                Log.i(TAG, "Removed items from resolveInfoList: $removeResult")
                val application = getApplication<Application>()
                val addResult = _activityBeanList.addAll(
                    ApplicationUtils.getActivityBeanList(
                        application,
                        packageName
                    ).toMutableList()
                )
                Log.i(TAG, "Added items to resolveInfoList: $addResult")
                Log.i(TAG, "New size of resolveInfoList is ${_activityBeanList.size}.")
                sortActivityBeanList()
                val currentIndex = _activityBeanList.indexOf(focusedItem)
                if (currentIndex in 0 until _activityBeanList.size) {
                    setFocusedItemIndex2(currentIndex)
                } else {
                    setFocusedItemIndex2(0)
                }
            }
        }
        updateFixedActivities(getApplication(), packageName)
    }

    fun sortActivityBeanList(locale: Locale = Locale.getDefault()) {
        val collator: Collator = Collator.getInstance(locale)
        _activityBeanList.sortWith { a, b ->
            collator.compare(a.label, b.label)
        }
    }

    fun updateFixedActivityList(index: Int?, item: ActivityBean?) {
        val targetIndex = index ?: _focusedItemIndex1.value
        if (targetIndex in 0..<numFixedActivity) {
            _fixedActivityBeanList[targetIndex] = item
            _fixedActivityRecordList[targetIndex] = if (item == null) {
                null
            } else {
                ActivityRecord(item.packageName, item.activityName)
            }
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    settingsRepository.saveFixedActivityRecord(_fixedActivityRecordList)
                }
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

    fun setFocusedItemIndex3(newValue: Int) {
        _focusedItemIndex3.update {
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

    fun getSelectedTvInputInfo(): TvInputInfo? {
        return if (_focusedItemIndex3.value >= 0 && _focusedItemIndex3.value < _tvInputList.size) {
            _tvInputList[_focusedItemIndex3.value]
        } else {
            null
        }
    }
}