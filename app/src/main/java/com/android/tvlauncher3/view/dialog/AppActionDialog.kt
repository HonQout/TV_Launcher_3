package com.android.tvlauncher3.view.dialog

import android.content.pm.ResolveInfo
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Text
import com.android.tvlauncher3.R
import com.android.tvlauncher3.utils.ApplicationUtils
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.AppActionButton

@Composable
fun AppActionDialog(
    resolveInfo: ResolveInfo,
    onDismissRequest: () -> Unit = {},
) {
    val tag = "AppActionDialog"
    val context = LocalContext.current
    val bgColor = Color.Black
    val shouldShowBelongToHint =
        remember { ApplicationUtils.shouldShowBelongToHint(context, resolveInfo) }
    val applicationType = remember { ApplicationUtils.getApplicationType(context, resolveInfo) }

    BackHandler {
        Log.i(tag, "Pressed back button.")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(20.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .focusable(enabled = false),
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
                        visible = shouldShowBelongToHint,
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        Column {
                            Text(
                                text = String.format(
                                    stringResource(R.string.activity_belongs_to),
                                    ApplicationUtils.getActivityLabel(context, resolveInfo)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.LightGray,
                                fontSize = 16.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Image(
                        bitmap = ApplicationUtils.getApplicationIcon(context, resolveInfo)
                            .toBitmap()
                            .asImageBitmap(),
                        contentDescription = "app icon",
                        modifier = Modifier
                            .size(size = 75.dp)
                            .graphicsLayer {
                                cameraDistance = 12f
                            },
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = ApplicationUtils.getApplicationLabel(context, resolveInfo),
                        modifier = Modifier,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ApplicationUtils.getPackageName(resolveInfo).toString(),
                        modifier = Modifier,
                        color = Color.LightGray,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ApplicationUtils.getVersionNameAndVersionCode(
                            context,
                            resolveInfo
                        )
                            .toString(),
                        modifier = Modifier,
                        color = Color.LightGray,
                        fontSize = 18.sp,
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
                            val result = IntentUtils.launchApp(
                                context,
                                resolveInfo.activityInfo.packageName,
                                true
                            )
                            if (result) {
                                onDismissRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.hint_cannot_launch_app,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppActionButton(
                        modifier = Modifier,
                        iconRes = R.drawable.baseline_delete_24,
                        labelRes = R.string.uninstall,
                        onShortClick = {
                            when (applicationType) {
                                ApplicationUtils.Companion.ApplicationType.UNKNOWN -> {
                                    Toast.makeText(
                                        context,
                                        R.string.hint_cannot_uninstall_unknown_type,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                ApplicationUtils.Companion.ApplicationType.SYSTEM -> {
                                    Toast.makeText(
                                        context,
                                        R.string.hint_cannot_uninstall_system_app,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                ApplicationUtils.Companion.ApplicationType.UPDATED_SYSTEM, ApplicationUtils.Companion.ApplicationType.USER -> {
                                    val result = IntentUtils.requestUninstallApp(
                                        context,
                                        resolveInfo.activityInfo.packageName
                                    )
                                    if (result) {
                                        onDismissRequest()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.hint_uninstall_request_failed,
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                            val result = IntentUtils.openApplicationDetailsPage(
                                context,
                                resolveInfo.activityInfo.packageName
                            )
                            if (result) {
                                onDismissRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.hint_cannot_launch_settings,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppActionButton(
                        modifier = Modifier,
                        iconRes = R.drawable.baseline_store_24,
                        labelRes = R.string.application_market,
                        onShortClick = {
                            val result = IntentUtils.openAppInMarket(
                                context,
                                resolveInfo.activityInfo.packageName
                            )
                            if (result) {
                                onDismissRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.hint_no_app_market_installed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )
                }
            }
        }
    }
}