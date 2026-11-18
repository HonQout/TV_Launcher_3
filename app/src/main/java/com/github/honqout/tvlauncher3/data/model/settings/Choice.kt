package com.github.honqout.tvlauncher3.data.model.settings

data class Choice(
    val key: String,
    val content: String,
    var chosen: Boolean
) {
    fun invertChosen() {
        this.chosen = !this.chosen
    }
}