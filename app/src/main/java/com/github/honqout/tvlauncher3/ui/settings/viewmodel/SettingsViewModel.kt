package com.github.honqout.tvlauncher3.ui.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.settings.InfoSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SettingGroupModel
import com.github.honqout.tvlauncher3.data.model.settings.SettingModel
import com.github.honqout.tvlauncher3.data.model.string.ResourceStr
import com.github.honqout.tvlauncher3.datastore.repository.SettingsRepository
import com.github.honqout.tvlauncher3.ui.settings.key.MySettingsKeys
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsRepository: SettingsRepository = SettingsRepository(
        context = getApplication()
    )

    private val _switchValues = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val switchValues: StateFlow<Map<String, Boolean>> = _switchValues.asStateFlow()
    private val _singleChoiceValues = MutableStateFlow<Map<String, Int>>(emptyMap())
    val singleChoiceValues: StateFlow<Map<String, Int>> = _singleChoiceValues.asStateFlow()
    private val _slideValues = MutableStateFlow<Map<String, Float>>(emptyMap())
    val slideValues: StateFlow<Map<String, Float>> = _slideValues.asStateFlow()
    private val _inputValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val inputValues: StateFlow<Map<String, String>> = _inputValues.asStateFlow()

    val settingList: List<SettingModel> = listOf(
        SettingGroupModel(
            key = "system",
            title = ResourceStr.StrFromResource(R.string.header_system),
            iconRes = null,
            summary = null,
            settingList = listOf(
                InfoSettingModel(
                    key = MySettingsKeys.DEFAULT_APP,
                    title = ResourceStr.StrFromResource(R.string.default_app),
                    iconRes = null,
                    summary = null
                )
            ),
        ),
        SettingGroupModel(
            key = "about",
            title = ResourceStr.StrFromResource(R.string.header_about),
            iconRes = null,
            summary = null,
            settingList = listOf(
                InfoSettingModel(
                    key = MySettingsKeys.VERSION,
                    title = ResourceStr.StrFromResource(R.string.version),
                    iconRes = null,
                    summary = ResourceStr.Str(
                        ApplicationUtils.getVersionName(
                            application.applicationContext,
                            application.applicationContext.packageName
                        ) ?: ""
                    ),
                ),
                InfoSettingModel(
                    key = MySettingsKeys.VISIT_GITHUB_PAGE,
                    title = ResourceStr.StrFromResource(R.string.visit_github_page),
                    iconRes = null,
                    summary = ResourceStr.StrFromResource(R.string.visit_github_page_summary)
                )
            )
        )
    )

    init {
        viewModelScope.launch {
            settingsRepository.loadAllSettings().collect { saved ->
                _switchValues.update { saved.switchValues }
                _singleChoiceValues.update { saved.singleChoiceValues }
                _slideValues.update { saved.slideValues }
                _inputValues.update { saved.inputValues }
            }
        }
    }

    fun updateSwitch(key: String, value: Boolean) {
        _switchValues.update { it + (key to value) }
        viewModelScope.launch {
            settingsRepository.putSwitch(key, value)
        }
    }

    fun updateSingleChoice(key: String, value: Int) {
        _singleChoiceValues.update { it + (key to value) }
        viewModelScope.launch {
            settingsRepository.putSingleChoice(key, value)
        }
    }

    fun updateSlide(key: String, value: Float) {
        _slideValues.update { it + (key to value) }
        viewModelScope.launch {
            settingsRepository.putSlide(key, value)
        }
    }

    fun updateInput(key: String, value: String) {
        _inputValues.update { it + (key to value) }
        viewModelScope.launch {
            settingsRepository.putInput(key, value)
        }
    }

    fun getSwitchValue(key: String, default: Boolean): Boolean {
        return switchValues.value[key] ?: default
    }

    fun getSingleChoiceValue(key: String, default: Int): Int {
        return singleChoiceValues.value[key] ?: default
    }

    fun getSlideValue(key: String, default: Float): Float {
        return slideValues.value[key] ?: default
    }

    fun getInputValue(key: String, default: String): String {
        return inputValues.value[key] ?: default
    }
}