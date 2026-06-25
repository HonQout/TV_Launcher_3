package com.github.honqout.tvlauncher3.activity.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.datastore.repository.IconItemsRepository
import com.github.honqout.tvlauncher3.datastore.repository.SettingsRepository
import com.github.honqout.tvlauncher3.datastore.repository.iconItemsDataStore
import com.github.honqout.tvlauncher3.dto.ActivityDto
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.ApplicationUtils.Companion.LauncherActivityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val settingsRepository = SettingsRepository(application)
    val iconItemsRepository = IconItemsRepository(application)

    // data
    val fixedIconItemList = application.iconItemsDataStore.data
        .map { it.itemsList }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            List(numFixedActivity) { null }
        )
    val fixedIconList: StateFlow<List<ActivityDto?>> = iconItemsRepository.itemsFlow
        .map { originalList ->
            originalList.map { item ->
                val resolveInfo = ApplicationUtils.getLauncherActivity(
                    application,
                    LauncherActivityType.NORMAL,
                    item.packageName,
                    item.activityName
                )
                resolveInfo?.let { ActivityDto.fromResolveInfo(application, resolveInfo) }
            }
        }
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
    private val _activityDtoList = mutableStateListOf<ActivityDto>()
    val activityDtoList: List<ActivityDto> = _activityDtoList
    private val _focusedItemIndex1 = MutableStateFlow<Int>(-1)
    val focusedItemIndex1: StateFlow<Int> = _focusedItemIndex1.asStateFlow()
    private val _focusedItemIndex2 = MutableStateFlow<Int>(-1)
    val focusedItemIndex2: StateFlow<Int> = _focusedItemIndex2.asStateFlow()
    private val _selectedActivityDto = MutableStateFlow<ActivityDto?>(null)
    val selectedActivityDto: StateFlow<ActivityDto?> = _selectedActivityDto.asStateFlow()

    // broadcast receiver
    private var localeBroadcastReceiver: BroadcastReceiver? = null
    private var packageBroadcastReceiver: BroadcastReceiver? = null

    // mutex
    private val fixedActivityListMutex = Mutex()
    private val activityDtoListMutex = Mutex()

    companion object {
        const val TAG: String = "LauncherViewModel"

        enum class ListOp {
            INIT, ADD, REMOVE, REPLACE
        }
    }

    init {
        registerLocaleBR()
        registerPackageBR()
        loadActivityDtoList()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        unregisterLocaleBR()
        unregisterPackageBR()
        super.onCleared()
    }

    fun registerLocaleBR() {
        if (localeBroadcastReceiver != null) {
            return
        }

        val context = getApplication<Application>()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }

        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        localeBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                loadActivityDtoList()
            }
        }

        ContextCompat.registerReceiver(context, localeBroadcastReceiver, filter, receiverFlags)
    }

    fun unregisterLocaleBR() {
        localeBroadcastReceiver?.let {
            val context = getApplication<Application>()
            context.unregisterReceiver(it)
            localeBroadcastReceiver = null
        }
    }

    fun registerPackageBR() {
        if (packageBroadcastReceiver != null) {
            return
        }

        val context = getApplication<Application>()

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
                                updateActivityDtoList(ListOp.ADD, packageName)
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
                                updateActivityDtoList(ListOp.REMOVE, packageName)
                            } else {
                                Log.e(TAG, "Cannot get packageName.")
                            }
                        }
                    }

                    Intent.ACTION_PACKAGE_REPLACED -> {
                        val packageName = intent.data?.schemeSpecificPart
                        if (packageName != null) {
                            updateActivityDtoList(ListOp.REPLACE, packageName)
                        } else {
                            Log.e(TAG, "Cannot get packageName.")
                        }
                    }
                }
            }
        }

        ContextCompat.registerReceiver(context, packageBroadcastReceiver, filter, receiverFlags)
    }

    fun unregisterPackageBR() {
        packageBroadcastReceiver?.let {
            val context = getApplication<Application>()
            context.unregisterReceiver(it)
            packageBroadcastReceiver = null
        }
    }

    fun loadActivityDtoList() {
        viewModelScope.launch {
            activityDtoListMutex.withLock {
                withContext(Dispatchers.Default) {
                    _activityDtoList.clear()
                    _activityDtoList.addAll(
                        ApplicationUtils.getActivityDtoList(
                            getApplication(),
                            LauncherActivityType.NORMAL,
                            null
                        )
                    )
                    sortActivityDtoList()
                }
            }
        }
    }

    fun setItemInFixedIconList(position: Int?, item: ActivityDto?) {
        viewModelScope.launch {
            val targetPosition = position ?: _focusedItemIndex1.value
            if (targetPosition in 0..<numFixedActivity)
                IconItemsRepository(application).setIconByIndex(
                    targetPosition,
                    item?.packageName ?: "",
                    item?.activityName ?: ""
                )
        }
    }

    fun refreshItemsInFixedIconList(packageName: String) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            fixedActivityListMutex.withLock {
                withContext(Dispatchers.Default) {
                    for (i in 0..<numFixedActivity) {
                        val item = fixedIconList.value[i]
                        if (item != null) {
                            if (packageName == item.packageName) {
                                val resolveInfo = ApplicationUtils.getLauncherActivity(
                                    context,
                                    LauncherActivityType.NORMAL,
                                    item.packageName,
                                    item.activityName
                                )
                                if (resolveInfo == null) {
                                    iconItemsRepository.resetIconByIndex(i)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateActivityDtoList(op: ListOp, packageName: String) {
        viewModelScope.launch {
            activityDtoListMutex.withLock {
                withContext(Dispatchers.Default) {
                    // Save focused item
                    val focusedItem =
                        if (_focusedItemIndex2.value in _activityDtoList.indices) {
                            _activityDtoList[_focusedItemIndex2.value]
                        } else {
                            _activityDtoList[0]
                        }
                    // Initialize the whole list
                    if (op == ListOp.INIT || _activityDtoList.isEmpty()) {
                        _activityDtoList.clear()
                        _activityDtoList.addAll(
                            ApplicationUtils.getActivityDtoList(
                                getApplication(),
                                LauncherActivityType.NORMAL,
                                null
                            )
                        )
                        sortActivityDtoList()
                        return@withContext
                    }
                    // Remove
                    if (op == ListOp.REMOVE || op == ListOp.REPLACE) {
                        val removeResult = _activityDtoList.removeAll { activityDto ->
                            activityDto.packageName == packageName
                        }
                        Log.i(TAG, "Removed items from ActivityDto list: $removeResult")
                    }
                    // Add
                    if (op == ListOp.ADD || op == ListOp.REPLACE) {
                        val addResult = _activityDtoList.addAll(
                            ApplicationUtils.getActivityDtoList(
                                getApplication(),
                                LauncherActivityType.NORMAL,
                                packageName
                            ).toMutableList()
                        )
                        Log.i(TAG, "Added items to ActivityDto list: $addResult")
                        // Must sort the list after adding items
                        sortActivityDtoList()
                    }
                    // Restore focused item
                    val currentIndex =
                        if (op == ListOp.ADD || op == ListOp.REPLACE) {
                            _activityDtoList.indexOf(focusedItem)
                        } else {
                            _focusedItemIndex2.value
                        }
                    if (currentIndex in _activityDtoList.indices) {
                        setFocusedItemIndex2(currentIndex)
                    } else {
                        setFocusedItemIndex2(0)
                    }
                    // Update fixed activities
                    if (op == ListOp.REMOVE || op == ListOp.REPLACE) {
                        refreshItemsInFixedIconList(packageName)
                    }
                }
            }
        }
    }

    fun sortActivityDtoList() {
        val collator: Collator = Collator.getInstance()
        _activityDtoList.sortWith { a, b ->
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

    fun setSelectedActivityDto(newValue: ActivityDto) {
        _selectedActivityDto.update {
            newValue
        }
    }
}