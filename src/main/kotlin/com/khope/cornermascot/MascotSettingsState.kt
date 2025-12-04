package com.khope.cornermascot

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(
    name = "CornerMascotSettings",
    storages = [Storage("cornerMascot.xml")]
)
class MascotSettingsState : PersistentStateComponent<MascotSettingsState> {

    var imagePath: String = ""
    var mascotWidth: Int = 120
    var mascotHeight: Int = 120

    override fun getState(): MascotSettingsState = this

    override fun loadState(state: MascotSettingsState) {
        this.imagePath = state.imagePath
        this.mascotWidth = state.mascotWidth
        this.mascotHeight = state.mascotHeight
    }

    companion object {
        val instance: MascotSettingsState
            get() = service()
    }
}