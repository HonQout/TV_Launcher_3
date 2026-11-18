package com.github.honqout.tvlauncher3.data.model.settings

data class SavedSettings(
    val switchValues: Map<String, Boolean> = emptyMap(),
    val singleChoiceValues: Map<String, Int> = emptyMap(),
    val slideValues: Map<String, Float> = emptyMap(),
    val inputValues: Map<String, String> = emptyMap()
)