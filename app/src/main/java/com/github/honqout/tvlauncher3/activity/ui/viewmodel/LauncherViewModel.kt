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
import com.github.honqout.tvlauncher3.datastore.SettingsRepository
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.LauncherActivityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.text.Collator

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    // constant
    val numFixedActivity = 5
    val numColumns = 5

    // persistence
    val settingsRepository = SettingsRepository(getApplication())
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val fixedActivityListState: StateFlow<List<ActivityBean?>> = refreshSignal
        .flatMapLatest {
            settingsRepository.fixedActivityRecordFlow
        }
        .map { records ->
            records.map { record ->
                record?.let {
                    val resolveInfo = ApplicationUtils.getLauncherActivity(
                        getApplication(),
                        LauncherActivityType.NORMAL,
                        it.packageName,
                        it.activityName
                    )
                    resolveInfo?.let { ActivityBean(getApplication(), it) }
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            List(numFixedActivity) { null }
        )

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
    private val _showSettingsDialog = MutableStateFlow<Boolean>(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()
    private val _showAppListDialog = MutableStateFlow<Boolean>(false)
    val showAppListDialog: StateFlow<Boolean> = _showAppListDialog.asStateFlow()
    private val _showAppActionDialog = MutableStateFlow<Boolean>(false)
    val showAppActionDialog: StateFlow<Boolean> = _showAppActionDialog.asStateFlow()

    // data-related
    private val _activityBeanList = mutableStateListOf<ActivityBean>()
    val activityBeanList: List<ActivityBean> = _activityBeanList
    private val _focusedItemIndex1 = MutableStateFlow<Int>(-1)
    val focusedItemIndex1: StateFlow<Int> = _focusedItemIndex1.asStateFlow()
    private val _focusedItemIndex2 = MutableStateFlow<Int>(-1)
    val focusedItemIndex2: StateFlow<Int> = _focusedItemIndex2.asStateFlow()
    private val _selectedActivityBean = MutableStateFlow<ActivityBean?>(null)
    val selectedActivityBean: StateFlow<ActivityBean?> = _selectedActivityBean.asStateFlow()

    // broadcast receiver
    private var localeBroadcastReceiver: BroadcastReceiver? = null
    private var packageBroadcastReceiver: BroadcastReceiver? = null

    // mutex
    private val fixedActivityListMutex = Mutex()
    private val activityBeanListMutex = Mutex()

    companion object {
        const val TAG: String = "LauncherViewModel"

        enum class ListOp {
            INIT, ADD, REMOVE, REPLACE
        }
    }

    init {
        registerLocaleBR(getApplication())
        registerPackageBR(getApplication())
        loadActivityBeanList()
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
                refreshSignal.tryEmit(Unit)
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
                                updateActivityBeanList(ListOp.ADD, packageName)
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
                                updateActivityBeanList(ListOp.REMOVE, packageName)
                            } else {
                                Log.e(TAG, "Cannot get packageName.")
                            }
                        }
                    }

                    Intent.ACTION_PACKAGE_REPLACED -> {
                        val packageName = intent.data?.schemeSpecificPart
                        if (packageName != null) {
                            updateActivityBeanList(ListOp.REPLACE, packageName)
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

    fun loadActivityBeanList() {
        viewModelScope.launch {
            activityBeanListMutex.withLock {
                withContext(Dispatchers.Default) {
                    _activityBeanList.clear()
                    _activityBeanList.addAll(
                        ApplicationUtils.getActivityBeanList(
                            getApplication(),
                            LauncherActivityType.NORMAL,
                            null
                        )
                    )
                    sortActivityBeanList()
                }
            }
        }
    }

    fun addItemToFixedActivityBeanList(index: Int?, item: ActivityBean?) {
        viewModelScope.launch {
            fixedActivityListMutex.withLock {
                withContext(Dispatchers.Default) {
                    val targetIndex = index ?: _focusedItemIndex1.value
                    if (targetIndex in 0..<numFixedActivity) {
                        val list = fixedActivityListState.value.toMutableList()
                        list[targetIndex] = item
                        if (item == null) {
                            Log.i(TAG, "Set item $targetIndex of FixedItemList to null")
                        } else {
                            Log.i(
                                TAG,
                                "Set item $targetIndex of FixedItemList to activity ${item.getKey()}"
                            )
                        }
                        withContext(Dispatchers.IO) {
                            settingsRepository.saveFixedActivityRecord(list)
                        }
                    }
                }
            }
        }
    }

    fun refreshItemsOfFixedActivityBeanList(packageName: String) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            fixedActivityListMutex.withLock {
                withContext(Dispatchers.Default) {
                    val list = mutableListOf<ActivityRecord?>()
                    for (i in 0..<numFixedActivity) {
                        var item = fixedActivityListState.value[i]
                        if (item != null) {
                            if (packageName == item.packageName) {
                                val resolveInfo = ApplicationUtils.getLauncherActivity(
                                    context,
                                    LauncherActivityType.NORMAL,
                                    item.packageName,
                                    item.activityName
                                )
                                item = if (resolveInfo == null) {
                                    null
                                } else {
                                    ActivityBean(context, resolveInfo)
                                }
                            }
                            list.add(item as ActivityRecord)
                        } else {
                            list.add(null)
                        }
                    }
                    withContext(Dispatchers.IO) {
                        settingsRepository.saveFixedActivityRecord(list)
                    }
                }
            }
        }
    }

    fun updateActivityBeanList(op: ListOp, packageName: String) {
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
                    if (op == ListOp.INIT || _activityBeanList.isEmpty()) {
                        _activityBeanList.clear()
                        _activityBeanList.addAll(
                            ApplicationUtils.getActivityBeanList(
                                getApplication(),
                                LauncherActivityType.NORMAL,
                                null
                            )
                        )
                        sortActivityBeanList()
                        return@withContext
                    }
                    // Remove
                    if (op == ListOp.REMOVE || op == ListOp.REPLACE) {
                        val removeResult = _activityBeanList.removeAll { activityBean ->
                            activityBean.packageName == packageName
                        }
                        Log.i(TAG, "Removed items from ActivityBeanList: $removeResult")
                    }
                    // Add
                    if (op == ListOp.ADD || op == ListOp.REPLACE) {
                        val addResult = _activityBeanList.addAll(
                            ApplicationUtils.getActivityBeanList(
                                getApplication(),
                                LauncherActivityType.NORMAL,
                                packageName
                            ).toMutableList()
                        )
                        Log.i(TAG, "Added items to ActivityBeanList: $addResult")
                        // Must sort the list after adding items
                        sortActivityBeanList()
                    }
                    // Restore focused item
                    val currentIndex =
                        if (op == ListOp.ADD || op == ListOp.REPLACE) {
                            _activityBeanList.indexOf(focusedItem)
                        } else {
                            _focusedItemIndex2.value
                        }
                    if (currentIndex in 0 until _activityBeanList.size) {
                        setFocusedItemIndex2(currentIndex)
                    } else {
                        setFocusedItemIndex2(0)
                    }
                    // Update fixed activities
                    if (op == ListOp.REMOVE || op == ListOp.REPLACE) {
                        refreshItemsOfFixedActivityBeanList(packageName)
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
        _showSettingsDialog.update {
            newValue
        }
    }

    fun setShowAppListScreen(newValue: Boolean) {
        _showAppListDialog.update {
            newValue
        }
    }

    fun setShowAppActionScreen(newValue: Boolean) {
        _showAppActionDialog.update {
            newValue
        }
    }

    fun setFocusedItemIndex1(newValue: Int) {
        _focusedItemIndex1.update {
            newValue
        }
        Log.i(TAG, "FocusedItemIndex1 is set to ${_focusedItemIndex1.value}.")
    }

    fun setFocusedItemIndex2(newValue: Int) {
        _focusedItemIndex2.update {
            newValue
        }
    }

    fun setSelectedActivityBean(newValue: ActivityBean) {
        _selectedActivityBean.update {
            newValue
        }
    }
}