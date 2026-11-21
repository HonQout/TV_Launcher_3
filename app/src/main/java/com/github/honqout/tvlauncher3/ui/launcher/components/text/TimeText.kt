package com.github.honqout.tvlauncher3.ui.launcher.components.text

import android.text.format.DateUtils
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.TimeViewModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M

@Composable
fun TimeText(
    modifier: Modifier = Modifier,
    viewModel: TimeViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner),
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = FONT_SIZE_M,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val context = LocalContext.current
    val currentTime by viewModel.currentTime.collectAsState()

    val formattedDate = remember(currentTime) {
        DateUtils.formatDateTime(
            context,
            currentTime,
            DateUtils.FORMAT_SHOW_TIME
        )
    }

    Text(
        text = formattedDate,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}