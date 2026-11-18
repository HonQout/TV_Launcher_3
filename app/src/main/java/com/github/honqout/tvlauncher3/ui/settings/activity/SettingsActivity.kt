package com.github.honqout.tvlauncher3.ui.settings.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.button.ButtonModel
import com.github.honqout.tvlauncher3.data.model.settings.InfoSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.InputSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SettingGroupModel
import com.github.honqout.tvlauncher3.data.model.settings.SingleChoiceSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SlideSettingModel
import com.github.honqout.tvlauncher3.data.model.settings.SwitchSettingModel
import com.github.honqout.tvlauncher3.data.model.string.ResourceStr
import com.github.honqout.tvlauncher3.ui.general.components.dialog.AlertDialogTv
import com.github.honqout.tvlauncher3.ui.settings.components.dialog.InputDialog
import com.github.honqout.tvlauncher3.ui.settings.components.dialog.SingleChoiceDialog
import com.github.honqout.tvlauncher3.ui.settings.components.dialog.SlideDialog
import com.github.honqout.tvlauncher3.ui.settings.components.screen.SettingsScreen
import com.github.honqout.tvlauncher3.ui.settings.key.MySettingsKeys
import com.github.honqout.tvlauncher3.ui.settings.viewmodel.SettingsViewModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_3XL
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_L
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S
import com.github.honqout.tvlauncher3.ui.theme.TVLauncher3Theme
import com.github.honqout.tvlauncher3.utils.IntentUtils
import com.github.honqout.tvlauncher3.utils.UIUtils

class SettingsActivity : ComponentActivity() {
    companion object {
        private const val TAG: String = "SettingsActivity"
    }

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var pendingSingleChoice: SingleChoiceSettingModel? by mutableStateOf(null)
    private var pendingSlide: SlideSettingModel? by mutableStateOf(null)
    private var pendingInput: InputSettingModel? by mutableStateOf(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide status bar and navigation bar
        UIUtils.handleSystemBarsVisibility(window, false)

        setContent {
            DisposableEffect(Unit) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    ) {
                        true
                    },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    ) {
                        true
                    }
                )
                onDispose {}
            }

            TVLauncher3Theme {
                var showVisitGithubPageDialog by rememberSaveable { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = getString(R.string.settings),
                                    fontSize = FONT_SIZE_3XL
                                )
                            },
                            colors = topAppBarColors(
                                containerColor = Color.Black,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    containerColor = Color.Black
                ) { innerPadding ->
                    val settingList = settingsViewModel.settingList

                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        settingList = settingList,
                        onItemClicked = { item ->
                            when (item) {
                                is InfoSettingModel -> {
                                    if (item.key == MySettingsKeys.DEFAULT_APP) {
                                        launchManageDefaultAppsSettings()
                                    } else if (item.key == MySettingsKeys.VISIT_GITHUB_PAGE) {
                                        showVisitGithubPageDialog = true
                                    }
                                }

                                is SwitchSettingModel -> {
                                    // Require this to be empty
                                }

                                is SingleChoiceSettingModel -> {
                                    pendingSingleChoice = item
                                }

                                is SlideSettingModel -> {
                                    pendingSlide = item
                                }

                                is InputSettingModel -> {
                                    pendingInput = item
                                }

                                is SettingGroupModel -> {
                                    // Require this to be empty
                                }
                            }
                        },
                        onItemSwitched = { key, newValue ->
                            settingsViewModel.updateSwitch(key, newValue)
                        },
                        onItemChosen = { key, newValue ->
                            // This is not actually used, preference saving process is handled by
                            // the dialog
                        },
                        onItemSlid = { key, newValue ->
                            // This is not actually used, preference saving process is handled by
                            // the dialog
                        },
                        onItemInput = { key, newValue ->
                            // This is not actually used, preference saving process is handled by
                            // the dialog
                        }
                    )
                }

                pendingSingleChoice?.let { item ->
                    SingleChoiceDialog(
                        item = item,
                        onDismiss = { pendingSingleChoice = null },
                        onConfirm = { value ->
                            settingsViewModel.updateSingleChoice(item.key, value)
                            pendingSingleChoice = null
                        }
                    )
                }

                pendingSlide?.let { item ->
                    SlideDialog(
                        item = item,
                        onDismiss = { pendingSlide = null },
                        onConfirm = { value ->
                            settingsViewModel.updateSlide(item.key, value)
                            pendingSlide = null
                        }
                    )
                }

                pendingInput?.let { item ->
                    InputDialog(
                        item = item,
                        onDismiss = { pendingInput = null },
                        onConfirm = { value ->
                            settingsViewModel.updateInput(item.key, value)
                            pendingInput = null
                        }
                    )
                }

                if (showVisitGithubPageDialog) {
                    AlertDialogTv(
                        onDismissRequest = {
                            showVisitGithubPageDialog = false
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.visit_github_page),
                                color = Color.White,
                                fontSize = FONT_SIZE_L,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        content = {
                            Text(
                                text = stringResource(R.string.visit_github_page_message),
                                color = Color.LightGray,
                                fontSize = FONT_SIZE_S,
                                fontWeight = FontWeight.SemiBold,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 5
                            )
                        },
                        confirmButton = ButtonModel(
                            text = ResourceStr.StrFromResource(R.string.visit),
                            onClick = {
                                IntentUtils.openWebsite(
                                    this,
                                    getString(R.string.github_address),
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                                showVisitGithubPageDialog = false
                            }
                        ),
                        dismissButton = ButtonModel(
                            text = ResourceStr.StrFromResource(R.string.cancel),
                            onClick = {
                                showVisitGithubPageDialog = false
                            }
                        )
                    )
                }
            }
        }
    }

    private fun launchManageDefaultAppsSettings(): Unit {
        val intent: Intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.cannot_find_activity, Toast.LENGTH_SHORT).show()
        }
    }
}