package com.android.tvlauncher3.view.button

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.bean.ActivityBean
import com.android.tvlauncher3.constants.ColorConstants
import com.android.tvlauncher3.utils.IntentUtils

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    index: Int,
    item: ActivityBean
) {
    val tag = "AppButton"
    val context = LocalContext.current

    RoundRectButton(
        modifier = modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    Log.i(tag, "FocusedItemIndex: $index")
                    viewModel.setFocusedItemIndex2(index)
                    viewModel.setFocusedItemResolveInfo(item.resolveInfo)
                }
            },
        icon = item.getIcon(context),
        iconType = item.iconType,
        label = item.label,
        contentDefaultColor = ColorConstants.ButtonContentDefault,
        contentFocusedColor = ColorConstants.ButtonContentFocused,
        onShortClick = {
            val packageName = item.packageName
            val result: Boolean =
                IntentUtils.launchApp(context, packageName, true)
            if (!result) {
                Toast.makeText(
                    context,
                    ContextCompat.getString(
                        context,
                        R.string.hint_cannot_launch_app
                    ),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        },
        onLongClick = {
            viewModel.setPressedItemResolveInfo(item.resolveInfo)
            viewModel.setResolveInfo(item.resolveInfo)
            viewModel.setShowAppActionDialog(true)
        }
    )
}