package com.android.tvlauncher3.view.dialog

import android.content.Context
import android.content.pm.ResolveInfo
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Text
import com.android.tvlauncher3.R
import com.android.tvlauncher3.utils.ApplicationUtils
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.AppActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppActionDialog(
    context: Context,
    resolveInfo: ResolveInfo,
    onDismissRequest: () -> Unit = {},
) {
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val shouldShowBelongToHint =
        remember { ApplicationUtils.shouldShowBelongToHint(context, resolveInfo) }
    val applicationType = remember { ApplicationUtils.getApplicationType(context, resolveInfo) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(16.dp))
            .padding(20.dp)
            .height(IntrinsicSize.Max)
            .width(IntrinsicSize.Max),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Box(
            modifier = Modifier
                .heightIn(max = 600.dp)
                .widthIn(max = 600.dp)
                .wrapContentSize()
                .fillMaxSize(),
        ) {
            Column {
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
                            color = Color.Black,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .focusProperties { canFocus = false },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = ApplicationUtils.getApplicationIcon(context, resolveInfo)
                            .toBitmap()
                            .asImageBitmap(),
                        contentDescription = "app icon",
                        modifier = Modifier
                            .size(size = 60.dp)
                            .graphicsLayer {
                                cameraDistance = 12f
                            },
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = ApplicationUtils.getApplicationLabel(context, resolveInfo),
                            modifier = Modifier,
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = ApplicationUtils.getPackageName(resolveInfo).toString(),
                            modifier = Modifier,
                            color = Color.Black,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = ApplicationUtils.getActivityName(resolveInfo).toString(),
                            modifier = Modifier,
                            color = Color.Black,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = ApplicationUtils.getVersionNameAndVersionCode(
                                context,
                                resolveInfo
                            )
                                .toString(),
                            modifier = Modifier,
                            color = Color.Black,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .focusGroup(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppActionButton(
                        modifier = Modifier
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    else -> false
                                }
                            },
                        iconRes = R.drawable.baseline_play_circle_filled_24,
                        label = stringResource(R.string.run),
                        onShortClick = {
                            val launchResult = IntentUtils.launchApp(
                                context,
                                resolveInfo.activityInfo.packageName,
                                true
                            )
                            if (launchResult) {
                                onDismissRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    ContextCompat.getString(
                                        context,
                                        R.string.hint_cannot_launch_app
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    AppActionButton(
                        modifier = Modifier
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    else -> false
                                }
                            },
                        iconRes = R.drawable.baseline_delete_24,
                        label = stringResource(R.string.uninstall),
                        onShortClick = {
                            IntentUtils.requestUninstallApp(
                                context,
                                resolveInfo.activityInfo.packageName
                            )
                            onDismissRequest()
                        },
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    AppActionButton(
                        modifier = Modifier
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    else -> false
                                }
                            },
                        iconRes = R.drawable.baseline_info_24,
                        label = stringResource(R.string.info),
                        onShortClick = {
                            IntentUtils.openApplicationSettingsPage(
                                context,
                                resolveInfo.activityInfo.packageName
                            )
                            onDismissRequest()
                        },
                    )
                }
            }
        }
    }
}