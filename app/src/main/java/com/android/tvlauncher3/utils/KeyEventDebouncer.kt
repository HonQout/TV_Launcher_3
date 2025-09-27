package com.android.tvlauncher3.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

class KeyEventDebouncer(private val debounceTime: Long = 300L) {
    private val lastKeyEventTimes = mutableMapOf<Int, Long>()

    fun shouldProcessKeyEvent(keyCode: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastTime = lastKeyEventTimes[keyCode] ?: 0L

        return if (currentTime - lastTime > debounceTime) {
            lastKeyEventTimes[keyCode] = currentTime
            true
        } else {
            false
        }
    }

    fun clear() {
        lastKeyEventTimes.clear()
    }
}

@Composable
fun rememberKeyEventDebouncer(debounceTime: Long = 300L): KeyEventDebouncer {
    return remember {
        KeyEventDebouncer(debounceTime)
    }.apply {
        DisposableEffect(Unit) {
            onDispose {
                clear()
            }
        }
    }
}