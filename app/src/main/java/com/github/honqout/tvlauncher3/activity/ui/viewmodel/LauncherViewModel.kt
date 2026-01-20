package com.github.honqout.tvlauncher3.activity.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import java.text.Collator

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
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
    private val _showSettingsScreen = MutableStateFlow<Boolean>(false)
    val showSettingsScreen: StateFlow<Boolean> = _showSettingsScreen.asStateFlow()
    private val _showAppListScreen = MutableStateFlow<Boolean>(false)
    val showAppListScreen: StateFlow<Boolean> = _showAppListScreen.asStateFlow()
    private val _showAppActionScreen = MutableStateFlow<Boolean>(false)
    val showAppActionScreen: StateFlow<Boolean> = _showAppActionScreen.asStateFlow()

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
    private val _activityBean = MutableStateFlow<ActivityBean?>(null)
    val activityBean: StateFlow<ActivityBean?> = _activityBean.asStateFlow()

    // broadcast receiver
    private var localeBroadcastReceiver: BroadcastReceiver? = null
    private var packageBroadcastReceiver: BroadcastReceiver? = null

    // mutex
    private val fixedActivityListMutex = Mutex()
    private val activityBeanListMutex = Mutex()

    companion object {
        const val TAG: String = "LauncherViewModel"

        enum class ActivityBeanListOp {
            INIT, ADD, REMOVE, REPLACE
        }
    }

    init {
        _isInitializing.value = true
        registerLocaleBR(getApplication())
        registerPackageBR(getApplication())
        loadFixedActivityList()
        loadActivityBeanList()
        _isInitializing.value = false
    }

    override fun onCleared() {
        viewModelScope.cancel()
        unregisterLocaleBR(getApplication())
        unregisterPackageBR(getApplication())
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
                loadFixedActivityList()
                loadActivityBeanList()
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

        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        packageBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) {
                    Log.i(TAG, "Received intent is null.")
                    return
                }
                val action: String = intent.action ?: return
                Log.i(TAG, "Received intent action: $action")
                when (action) {
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        Log.i(TAG, "Extra: replacing = $replacing")
                        if (!replacing) {
                            val packageName = intent.data?.schemeSpecificPart
                            if (packageName != null) {
                                updateActivityBeanList(ActivityBeanListOp.ADD, packageName)
                            } else {
                                Log.e(TAG, "Cannot get packageName.")
                            }
                        }
                    }

                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        val dataRemoved =
                            intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false)
                        Log.i(TAG, "Extra: replacing = $replacing, data_removed = $dataRemoved")
                        if (!replacing) {
                            val packageName = intent.data?.schemeSpecificPart
                            if (packageName != null) {
                                updateActivityBeanList(ActivityBeanListOp.REMOVE, packageName)
                            } else {
                                Log.e(TAG, "Cannot get packageName.")
                            }
                        }
                    }

                    Intent.ACTION_PACKAGE_REPLACED -> {
                        val packageName = intent.data?.schemeSpecificPart
                        if (packageName != null) {
                            updateActivityBeanList(ActivityBeanListOp.REPLACE, packageName)
                        } else {
                            Log.e(TAG, "Cannot get packageName.")
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
                fixedActivityListMutex.withLock {
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
    }

    fun loadActivityBeanList() {
        viewModelScope.launch {
            activityBeanListMutex.withLock {
                withContext(Dispatchers.Default) {
                    _activityBeanList.clear()
                    _activityBeanList.addAll(
                        ApplicationUtils.getActivityBeanList(
                            getApplication(),
                            null
                        )
                    )
                    sortActivityBeanList()
                }
            }
        }
    }

    fun updateFixedActivities(context: Context, packageName: String) {
        viewModelScope.launch {
            fixedActivityListMutex.withLock {
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
                withContext(Dispatchers.IO) {
                    settingsRepository.saveFixedActivityRecord(_fixedActivityRecordList)
                }
            }
        }
    }

    fun updateActivityBeanList(op: ActivityBeanListOp, packageName: String) {
        viewModelScope.launch {
            activityBeanListMutex.withLock {
                withContext(Dispatchers.Default) {
                    // Save focused item
                    val focusedItem =
                        if (_focusedItemIndex2.value in 0 until _activityBeanList.size) {
                            _activityBeanList[_focusedItemIndex2.value]
                        } else {
                            _activityBeanList[0]
                        }
                    // Prepare the whole list
                    if (op == ActivityBeanListOp.INIT || _activityBeanList.isEmpty()) {
                        _activityBeanList.clear()
                        _activityBeanList.addAll(
                            ApplicationUtils.getActivityBeanList(
                                getApplication(),
                                null
                            )
                        )
                        sortActivityBeanList()
                        return@withContext
                    }
                    // Remove
                    if (op == ActivityBeanListOp.REMOVE || op == ActivityBeanListOp.REPLACE) {
                        val removeResult = _activityBeanList.removeAll { activityBean ->
                            activityBean.packageName == packageName
                        }
                        Log.i(TAG, "Removed items from ActivityBeanList: $removeResult")
                    }
                    // Add
                    if (op == ActivityBeanListOp.ADD || op == ActivityBeanListOp.REPLACE) {
                        val addResult = _activityBeanList.addAll(
                            ApplicationUtils.getActivityBeanList(
                                getApplication(),
                                packageName
                            ).toMutableList()
                        )
                        Log.i(TAG, "Added items to ActivityBeanList: $addResult")
                        // Must sort the list after adding items
                        sortActivityBeanList()
                    }
                    // Restore focused item
                    val currentIndex = _activityBeanList.indexOf(focusedItem)
                    if (currentIndex in 0 until _activityBeanList.size) {
                        setFocusedItemIndex2(currentIndex)
                    } else {
                        setFocusedItemIndex2(0)
                    }
                    // Update fixed activities
                    if (op == ActivityBeanListOp.REMOVE || op == ActivityBeanListOp.REPLACE) {
                        updateFixedActivities(getApplication(), packageName)
                    }
                }
            }
        }
    }

    fun sortActivityBeanList() {
        val collator: Collator = Collator.getInstance()
        _activityBeanList.sortWith { a, b ->
            collator.compare(a.label, b.label)
        }
    }

    fun updateFixedActivityList(index: Int?, item: ActivityBean?) {
        viewModelScope.launch {
            fixedActivityListMutex.withLock {
                withContext(Dispatchers.Default) {
                    val targetIndex = index ?: _focusedItemIndex1.value
                    if (targetIndex in 0..<numFixedActivity) {
                        _fixedActivityBeanList[targetIndex] = item
                        _fixedActivityRecordList[targetIndex] = if (item == null) {
                            null
                        } else {
                            ActivityRecord(item.packageName, item.activityName)
                        }
                        withContext(Dispatchers.IO) {
                            settingsRepository.saveFixedActivityRecord(_fixedActivityRecordList)
                        }
                    }
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

    fun setShowSettingsScreen(newValue: Boolean) {
        _showSettingsScreen.update {
            newValue
        }
    }

    fun setShowAppListScreen(newValue: Boolean) {
        _showAppListScreen.update {
            newValue
        }
    }

    fun setShowAppActionScreen(newValue: Boolean) {
        _showAppActionScreen.update {
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

    fun setActivityBean(newValue: ActivityBean) {
        _activityBean.update {
            newValue
        }
    }
}