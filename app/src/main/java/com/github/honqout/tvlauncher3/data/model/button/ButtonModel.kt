package com.github.honqout.tvlauncher3.data.model.button

import com.github.honqout.tvlauncher3.data.model.string.ResourceStr

data class ButtonModel(
    var text: ResourceStr,
    var onClick: () -> Unit
)