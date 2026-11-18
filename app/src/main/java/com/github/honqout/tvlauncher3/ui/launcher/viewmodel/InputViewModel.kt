package com.github.honqout.tvlauncher3.ui.launcher.viewmodel

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.honqout.tvlauncher3.data.repository.InputRepository
import com.github.honqout.tvlauncher3.utils.TvUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InputViewModel(application: Application) : AndroidViewModel(application) {
    // UI-related
    private var oldConfig: Configuration? = null
    private val _selectedItemIndex = MutableStateFlow<Int>(0)
    val selectedItemIndex: StateFlow<Int> = _selectedItemIndex.asStateFlow()

    // data-related
    private val _topBarHeight = MutableStateFlow<Int>(0)
    val topBarHeight: StateFlow<Int> = _topBarHeight.asStateFlow()
    private val _tvInputList = MutableStateFlow<List<TvInputInfo>>(emptyList())
    val tvInputList: StateFlow<List<TvInputInfo>> = _tvInputList.asStateFlow()

    // repository
    private var inputRepository: InputRepository

    companion object {
        const val TAG: String = "InputViewModel"

        enum class InputListOp {
            INIT,
            ADD,
            REMOVE,
            UPDATE
        }
    }

    init {
        inputRepository = InputRepository(
            context = getApplication(),
            callback = object : TvInputManager.TvInputCallback() {
                override fun onInputAdded(inputId: String?) {
                    loadTvInputList()
                }

                override fun onInputRemoved(inputId: String?) {
                    loadTvInputList()
                }

                override fun onInputStateChanged(inputId: String?, state: Int) {
                    loadTvInputList()
                }

                override fun onInputUpdated(inputId: String?) {
                    loadTvInputList()
                }

                override fun onTvInputInfoUpdated(inputInfo: TvInputInfo?) {
                    loadTvInputList()
                }
            }
        )
        inputRepository.register()
        loadTvInputList()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        inputRepository.unregister()
    }

    fun loadTvInputList() {
        viewModelScope.launch(Dispatchers.Default) {
            _tvInputList.value = TvUtils.getTvInputList(getApplication())
        }
    }

    fun onConfigChanged(newConfig: Configuration) {
        oldConfig?.let {
            val diff = it.diff(newConfig)
            if ((diff and ActivityInfo.CONFIG_LOCALE) != 0) {
                loadTvInputList()
            }
        }
        oldConfig = newConfig
    }

    fun setTopBarHeight(newValue: Int) {
        _topBarHeight.update {
            newValue
        }
    }

    fun setSelectedItemIndex(newValue: Int) {
        _selectedItemIndex.update {
            newValue
        }
    }

    fun getSelectedTvInputInfo(): TvInputInfo? {
        return if (_selectedItemIndex.value >= 0 && _selectedItemIndex.value < _tvInputList.value.size) {
            _tvInputList.value[_selectedItemIndex.value]
        } else {
            null
        }
    }
}