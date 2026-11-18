package com.github.honqout.tvlauncher3.ui.launcher.viewmodel

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.pm.LauncherApps
import android.content.res.Configuration
import android.os.Process
import android.os.UserHandle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.activity.ActivityModel
import com.github.honqout.tvlauncher3.data.repository.AppRepository
import com.github.honqout.tvlauncher3.datastore.repository.IconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Collator
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    application: Application,
    private val iconRepository: IconRepository
) : AndroidViewModel(application) {
    // constant
    val numColumns = 5

    // UI-related
    val tabs = listOf(
        Pair(R.drawable.baseline_home_24, R.string.home),
        Pair(R.drawable.baseline_apps_24, R.string.apps),
        Pair(R.drawable.baseline_input_24, R.string.input)
    )
    private var oldConfig: Configuration? = null
    private val _topBarHeight = MutableStateFlow<Int>(0)
    val topBarHeight: StateFlow<Int> = _topBarHeight.asStateFlow()
    private val _selectedTabIndex = MutableStateFlow<Int>(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    // data
    private val refreshFixedIconListSignal = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(Unit)
    }
    val fixedIconList: StateFlow<List<ActivityModel?>> =
        combine(
            iconRepository.itemsFlow,
            refreshFixedIconListSignal
        ) { originalList, _ ->
            originalList.map { item ->
                appRepository.getActivityModel(
                    context = application,
                    packageName = item.packageName,
                    activityName = item.activityName
                )
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                List(IconRepository.NUM_FIXED_ACTIVITY) { null }
            )
    private val _activityModelList = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activityModelList: StateFlow<List<ActivityModel>> = _activityModelList.asStateFlow()
    private var focusedFixedIconIndex = -1
    private val _focusedActivityItemIndex = MutableStateFlow<Int>(-1)
    val focusedActivityItemIndex: StateFlow<Int> = _focusedActivityItemIndex.asStateFlow()
    private val _selectedActivityModel = MutableStateFlow<ActivityModel?>(null)
    val selectedActivityModel: StateFlow<ActivityModel?> = _selectedActivityModel.asStateFlow()

    // repository
    private var appRepository: AppRepository

    companion object {
        const val TAG: String = "LauncherViewModel"

        enum class IconListOp {
            REMOVE_AFTER_UNINSTALL,
            REMOVE_AFTER_UPDATE
        }

        enum class AppListOp {
            INIT,
            ADD,
            REMOVE,
            REPLACE,
            DISABLE,
            ENABLE
        }
    }

    init {
        // Initialize app repository
        appRepository = AppRepository(
            context = getApplication(),
            callback = object : LauncherApps.Callback() {
                override fun onPackageAdded(packageName: String?, user: UserHandle?) {
                    if (user == Process.myUserHandle() && !packageName.isNullOrEmpty()) {
                        updateActivityModelList(AppListOp.ADD, packageName)
                    }
                }

                override fun onPackageChanged(packageName: String?, user: UserHandle?) {
                    if (user == Process.myUserHandle() && !packageName.isNullOrEmpty()) {
                        updateActivityModelList(AppListOp.REPLACE, packageName)
                    }
                }

                override fun onPackageRemoved(packageName: String?, user: UserHandle?) {
                    if (user == Process.myUserHandle() && !packageName.isNullOrEmpty()) {
                        updateActivityModelList(AppListOp.REMOVE, packageName)
                    }
                }

                override fun onPackagesAvailable(
                    packageNames: Array<out String?>?,
                    user: UserHandle?,
                    replacing: Boolean
                ) {

                }

                override fun onPackagesUnavailable(
                    packageNames: Array<out String?>?,
                    user: UserHandle?,
                    replacing: Boolean
                ) {

                }

                override fun onPackagesSuspended(
                    packageNames: Array<out String?>?,
                    user: UserHandle?
                ) {
                    if (user == Process.myUserHandle() && !packageNames.isNullOrEmpty()) {
                        for (packageName in packageNames) {
                            if (!packageName.isNullOrEmpty()) {
                                updateActivityModelList(AppListOp.DISABLE, packageName)
                            }
                        }
                    }
                }

                override fun onPackagesUnsuspended(
                    packageNames: Array<out String?>?,
                    user: UserHandle?
                ) {
                    if (user == Process.myUserHandle() && !packageNames.isNullOrEmpty()) {
                        for (packageName in packageNames) {
                            if (!packageName.isNullOrEmpty()) {
                                updateActivityModelList(AppListOp.ENABLE, packageName)
                            }
                        }
                    }
                }
            }
        )
        appRepository.register()
        updateActivityModelList(AppListOp.INIT, null)
        initializeIcons()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        appRepository.unregister()
    }

    fun initializeIcons() {
        viewModelScope.launch {
            try {
                iconRepository.initializeIcons()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize icons.", e)
            }
        }
    }

    fun setIcon(position: Int?, item: ActivityModel?) {
        viewModelScope.launch {
            val targetPosition = position ?: focusedFixedIconIndex
            if (targetPosition in 0..<IconRepository.NUM_FIXED_ACTIVITY)
                try {
                    iconRepository.setIconByIndex(
                        targetPosition,
                        item?.packageName ?: "",
                        item?.activityName ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to set icon.", e)
                }
        }
    }

    fun updateFixedIconList(op: IconListOp, packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (op) {
                IconListOp.REMOVE_AFTER_UNINSTALL -> {
                    iconRepository.resetIconAfterPackageRemoved(packageName)
                }

                IconListOp.REMOVE_AFTER_UPDATE -> {
                    iconRepository.resetIconAfterPackageReplaced(getApplication(), packageName)
                }
            }
        }
    }

    fun updateActivityModelList(op: AppListOp, packageName: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            // If op is AppListOp.INIT, we do not care about packageName
            if (op == AppListOp.INIT || _activityModelList.value.isEmpty()) {
                // Initialize the whole list
                val list = appRepository.getActivityModelList(
                    getApplication(),
                    null
                )
                _activityModelList.value = sortActivityModelList(list)
                return@launch
            }
            // Otherwise, we should check if packageName is null or empty
            if (packageName.isNullOrEmpty()) {
                Log.e(
                    TAG,
                    "Cannot perform the specified operation. " +
                            "The given packageName is null or empty."
                )
                return@launch
            }
            // Before performing operation, we should save the focused item first
            val focusedItem =
                if (_focusedActivityItemIndex.value in _activityModelList.value.indices) {
                    _activityModelList.value[_focusedActivityItemIndex.value]
                } else {
                    _activityModelList.value[0]
                }
            // Remove
            if (op == AppListOp.REMOVE || op == AppListOp.REPLACE || op == AppListOp.DISABLE) {
                _activityModelList.update { currentList ->
                    val mutableList = currentList.toMutableList()
                    val removeResult = mutableList.removeAll { activityModel ->
                        activityModel.packageName == packageName
                    }
                    Log.i(TAG, "Removed items from ActivityModel list: $removeResult")
                    mutableList
                }
            }
            // Add
            if (op == AppListOp.ADD || op == AppListOp.REPLACE || op == AppListOp.ENABLE) {
                _activityModelList.update { currentList ->
                    val mutableList = currentList.toMutableList()
                    val addResult = mutableList.addAll(
                        appRepository.getActivityModelList(
                            getApplication(),
                            packageName
                        )
                    )
                    Log.i(TAG, "Added items to ActivityModel list: $addResult")
                    // Must sort the list after adding items
                    sortActivityModelList(mutableList)
                }
            }
            // Restore focused item
            val currentIndex =
                if (op == AppListOp.ADD || op == AppListOp.REPLACE) {
                    _activityModelList.value.indexOf(focusedItem)
                } else {
                    _focusedActivityItemIndex.value
                }
            if (currentIndex in _activityModelList.value.indices) {
                setFocusedActivityItemIndex(currentIndex)
            } else {
                setFocusedActivityItemIndex(0)
            }
            // Update fixed activities
            if (op == AppListOp.REMOVE) {
                updateFixedIconList(IconListOp.REMOVE_AFTER_UNINSTALL, packageName)
            }
            if (op == AppListOp.REPLACE) {
                updateFixedIconList(IconListOp.REMOVE_AFTER_UPDATE, packageName)
            }
        }
    }

    fun sortActivityModelList(list: List<ActivityModel>): List<ActivityModel> {
        val collator = Collator.getInstance()
        return list.sortedWith(compareBy(collator) { it.label.lowercase() })
    }

    fun onConfigChanged(newConfig: Configuration) {
        oldConfig?.let {
            val diff = it.diff(newConfig)
            if ((diff and ActivityInfo.CONFIG_LOCALE) != 0) {
                updateActivityModelList(AppListOp.INIT, null)
                refreshFixedIconListSignal.tryEmit(Unit)
            }
        }
        oldConfig = newConfig
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

    fun setFocusedFixedIconIndex(newValue: Int) {
        focusedFixedIconIndex = newValue
    }

    fun setFocusedActivityItemIndex(newValue: Int) {
        _focusedActivityItemIndex.update {
            newValue
        }
    }

    fun setSelectedActivityModel(newValue: ActivityModel?) {
        _selectedActivityModel.update {
            newValue
        }
    }
}