package com.github.honqout.tvlauncher3.view.dialog

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
import androidx.compose.ui.unit.sp
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
import com.github.honqout.tvlauncher3.bean.ActivityBean
import com.github.honqout.tvlauncher3.coil.model.AppIconModel
import com.github.honqout.tvlauncher3.constants.UIConstants
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import com.github.honqout.tvlauncher3.utils.IntentUtils
import com.github.honqout.tvlauncher3.view.button.AppActionButtonTv

@Composable
fun AppActionDialog(
    item: ActivityBean,
    onDismissRequest: () -> Unit = {}
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
        onDismissRequest()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
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
                .padding(20.dp),
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
                    visible = item.showBelongToHint,
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
                            fontSize = UIConstants.FONT_SIZE_SMALL,
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
                    text = ApplicationUtils.getApplicationLabel(
                        context,
                        item.packageName
                    ),
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.packageName,
                    modifier = Modifier,
                    color = Color.LightGray,
                    fontSize = UIConstants.FONT_SIZE_LARGE,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = ApplicationUtils.getVersionNameAndVersionCode(
                        context,
                        item.packageName
                    ),
                    modifier = Modifier,
                    color = Color.LightGray,
                    fontSize = UIConstants.FONT_SIZE_LARGE,
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
                AppActionButtonTv(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_play_circle_filled_24,
                    labelRes = R.string.run,
                    onShortClick = {
                        IntentUtils.handleLaunchActivityResult(
                            context,
                            IntentUtils.launchActivity(
                                context,
                                item.packageName,
                                item.activityName,
                                true
                            )
                        )
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButtonTv(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_delete_24,
                    labelRes = R.string.uninstall,
                    onShortClick = {
                        when (item.appType) {
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
                                IntentUtils.handleLaunchIntentResult(
                                    context,
                                    IntentUtils.requestUninstallApp(
                                        context,
                                        item.packageName
                                    ),
                                    { onDismissRequest() }
                                )
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButtonTv(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_info_24,
                    labelRes = R.string.info,
                    onShortClick = {
                        IntentUtils.handleLaunchIntentResult(
                            context,
                            IntentUtils.launchApplicationDetailsSettings(
                                context,
                                item.packageName
                            ),
                            { onDismissRequest() }
                        )
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppActionButtonTv(
                    modifier = Modifier,
                    iconRes = R.drawable.baseline_store_24,
                    labelRes = R.string.app_market,
                    onShortClick = {
                        IntentUtils.handleLaunchIntentResult(
                            context,
                            IntentUtils.openAppInMarket(
                                context,
                                item.packageName
                            ),
                            { onDismissRequest() }
                        )
                    },
                )
            }
        }
    }
}