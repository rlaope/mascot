package com.khope.cornermascot

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CornerMascotSettings", storages = [Storage("cornerMascot.xml")])
class MascotSettingsState : PersistentStateComponent<MascotSettingsState> {

    var imagePath: String = ""

    override fun getState(): MascotSettingsState = this

    override fun loadState(state: MascotSettingsState) {
        this.imagePath = state.imagePath
    }

    companion object {
        val instance: MascotSettingsState
            get() = ApplicationManager.getApplication().getService(MascotSettingsState::class.java)
    }
}