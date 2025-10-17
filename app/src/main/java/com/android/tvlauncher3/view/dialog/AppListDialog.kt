package com.android.tvlauncher3.view.dialog

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.bean.ActivityPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListDialog(
    onDismissRequest: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val itemsPerPage = 20
    val numRows = 4
    val numColumns = 5
    val coroutineScope = rememberCoroutineScope()

    val pages = remember(viewModel.activityBeanList) {
        viewModel.activityBeanList.chunked(itemsPerPage).mapIndexed { index, beans ->
            ActivityPage(
                pageIndex = index,
                apps = beans + List(itemsPerPage - beans.size) { null }
            )
        }
    }

    var currentPage by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = currentPage
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    val focusManager = LocalFocusManager.current

    val keyEventCallback = remember {
        { keyEvent: KeyEvent ->
            when (keyEvent.key) {
                Key.DirectionLeft -> {
                    if (selectedIndex % numColumns == 0) {
                        if (currentPage > 0) {
                            coroutineScope.launch(Dispatchers.IO) {
                                pagerState.animateScrollToPage(currentPage - 1)
                            }
                            true
                        } else {
                            false
                        }
                    } else {
                        selectedIndex--
                        true
                    }
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
        selectedIndex = 0
    }

//    BasicAlertDialog(
//        onDismissRequest = onDismissRequest,
//        modifier = Modifier
//            .height(IntrinsicSize.Max)
//            .width(IntrinsicSize.Max)
//            .background(color = bgColor, shape = RoundedCornerShape(16.dp))
//            .padding(20.dp),
//        properties = DialogProperties(
//            dismissOnBackPress = true,
//            dismissOnClickOutside = true,
//            usePlatformDefaultWidth = true
//        )
//    ) {
//        Box(
//            modifier = Modifier
//                .heightIn(max = 800.dp)
//                .widthIn(max = 800.dp)
//                .wrapContentSize()
//                .fillMaxSize()
//        ) {
//
//        }
//    }
}