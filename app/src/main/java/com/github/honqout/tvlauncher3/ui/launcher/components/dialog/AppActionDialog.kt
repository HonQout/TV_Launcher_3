package com.github.honqout.tvlauncher3.ui.launcher.components.dialog

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.size.Precision
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.coil.model.AppIconModel
import com.github.honqout.tvlauncher3.data.model.activity.ActivityModel
import com.github.honqout.tvlauncher3.ui.launcher.components.button.AppActionButton
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_XL
import com.github.honqout.tvlauncher3.ui.theme.PADDING_DIALOG_EDGE
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.IntentUtils
import com.github.honqout.tvlauncher3.utils.IntentUtils.Companion.LaunchActivityResult
import com.github.honqout.tvlauncher3.utils.IntentUtils.Companion.LaunchIntentResult

@Composable
fun AppActionDialog(
    item: ActivityModel,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val defIcon = context.packageManager.defaultActivityIcon

    val imageRequest = remember(item.packageName) {
        ImageRequest.Builder(context)
            .data(AppIconModel(item.packageName))
            .precision(Precision.INEXACT)
            .allowHardware(true)
            .crossfade(false)
            .placeholder(defIcon)
            .error(defIcon)
            .fallback(defIcon)
            .build()
    }

    BackHandler {
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .focusable(enabled = false)
                .padding(PADDING_DIALOG_EDGE),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = item.shouldShowBelongToHint(),
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Column {
                        Text(
                            text = String.format(
                                stringResource(R.string.activity_belongs_to),
                                item.label
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.LightGray,
                            fontSize = FONT_SIZE_S,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                AsyncImage(
                    modifier = Modifier
                        .size(size = 75.dp)
                        .graphicsLayer {
                            cameraDistance = 12f
                        },
                    model = imageRequest,
                    contentDescription = item.label,
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.appLabel,
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = FONT_SIZE_XL,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.packageName,
                    modifier = Modifier,
                    color = Color.LightGray,
                    fontSize = FONT_SIZE_M,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.getFullVersion(),
                    modifier = Modifier,
                    color = Color.LightGray,
                    fontSize = FONT_SIZE_M,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .focusGroup(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppActionButton(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_play_circle_filled_24,
                    labelRes = R.string.run,
                    onShortClick = {
                        val result = IntentUtils.launchActivity(
                            context,
                            item.packageName,
                            item.activityName,
                            true
                        )
                        when (result) {
                            LaunchActivityResult.SUCCESS -> {

                            }

                            LaunchActivityResult.NOT_FOUND -> {
                                Toast.makeText(
                                    context,
                                    R.string.cannot_find_activity,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LaunchActivityResult.NOT_EXPORTED -> {
                                Toast.makeText(
                                    context,
                                    R.string.cannot_access_unexported_activity,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LaunchActivityResult.REQUIRE_EXTRA_PERMISSION -> {
                                Toast.makeText(
                                    context,
                                    R.string.activity_requires_extra_permission,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButton(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_delete_24,
                    labelRes = R.string.uninstall,
                    onShortClick = {
                        val type: ApplicationUtils.Companion.ApplicationType =
                            ApplicationUtils.getApplicationType(
                                context, item.packageName
                            )
                        when (type) {
                            ApplicationUtils.Companion.ApplicationType.UNKNOWN -> {
                                Toast.makeText(
                                    context,
                                    R.string.cannot_uninstall_unknown_type,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            ApplicationUtils.Companion.ApplicationType.SYSTEM -> {
                                Toast.makeText(
                                    context,
                                    R.string.cannot_uninstall_system_app,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            ApplicationUtils.Companion.ApplicationType.UPDATED_SYSTEM,
                            ApplicationUtils.Companion.ApplicationType.USER -> {
                                val result = IntentUtils.requestUninstallApp(
                                    context,
                                    item.packageName
                                )
                                when (result) {
                                    LaunchIntentResult.SUCCESS -> {

                                    }

                                    LaunchIntentResult.URI_IS_EMPTY -> {
                                        Toast.makeText(
                                            context,
                                            R.string.uri_is_empty,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    LaunchIntentResult.NO_MATCHING_ACTIVITY -> {
                                        Toast.makeText(
                                            context,
                                            R.string.no_matching_activity,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    LaunchIntentResult.REQUIRE_EXTRA_PERMISSION -> {
                                        Toast.makeText(
                                            context,
                                            R.string.activity_requires_extra_permission,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButton(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_info_24,
                    labelRes = R.string.info,
                    onShortClick = {
                        IntentUtils.launchApplicationDetailsSettings(context, item.packageName)
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButton(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_store_24,
                    labelRes = R.string.app_market,
                    onShortClick = {
                        val result = IntentUtils.openAppInMarket(
                            context,
                            item.packageName,
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        when (result) {
                            LaunchIntentResult.SUCCESS -> {

                            }

                            LaunchIntentResult.URI_IS_EMPTY -> {
                                Toast.makeText(
                                    context,
                                    R.string.uri_is_empty,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LaunchIntentResult.NO_MATCHING_ACTIVITY -> {
                                Toast.makeText(
                                    context,
                                    R.string.no_app_market_installed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LaunchIntentResult.REQUIRE_EXTRA_PERMISSION -> {
                                Toast.makeText(
                                    context,
                                    R.string.activity_requires_extra_permission,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                )
            }
        }
    }
}