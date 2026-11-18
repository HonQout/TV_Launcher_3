package com.github.honqout.tvlauncher3.ui.input.activity

import android.media.tv.TvContract
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.databinding.ActivityInputBinding
import com.github.honqout.tvlauncher3.utils.UIUtils

class InputActivity : AppCompatActivity() {
    lateinit var binding: ActivityInputBinding

    companion object {
        const val EXTRA_INPUT_ID = "extra_input_id"
        const val EXTRA_CHANNEL_URI = "extra_channel_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputId = intent.getStringExtra(EXTRA_INPUT_ID)
            ?: {
                Toast
                    .makeText(this, R.string.cannot_display_input_screen, Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
                .toString()
        val channelUriString = intent.getStringExtra(EXTRA_CHANNEL_URI)

        enableEdgeToEdge()
        UIUtils.handleSystemBarsVisibility(window, false)

        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputTvView.apply {
            if (channelUriString != null) {
                tune(inputId, channelUriString.toUri())
            } else {
                tune(inputId, TvContract.buildChannelUriForPassthroughInput(inputId))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_ESCAPE -> {
                finish()
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.inputTvView.reset()
    }
}