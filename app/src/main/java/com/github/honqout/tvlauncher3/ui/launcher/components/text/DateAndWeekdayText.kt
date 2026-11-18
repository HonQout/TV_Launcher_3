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
import androidx.compose.ui.unit.TextUnit
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_XS
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.TimeViewModel

@Composable
fun DateAndWeekdayText(
    modifier: Modifier = Modifier,
    viewModel: TimeViewModel,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = FONT_SIZE_XS
) {
    val context = LocalContext.current
    val currentTime by viewModel.currentTime.collectAsState()

    val formattedDate = remember(currentTime) {
        DateUtils.formatDateTime(
            context,
            currentTime,
            DateUtils.FORMAT_NO_YEAR or
                    DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_WEEKDAY or
                    DateUtils.FORMAT_ABBREV_WEEKDAY
        )
    }

    Text(
        text = formattedDate,
        modifier = modifier,
        color = color,
        fontSize = fontSize
    )
}