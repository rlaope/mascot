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

    var imagePath1: String = ""
    var imagePath2: String = ""
    var imagePath3: String = ""
    var imagePath4: String = ""
    var currentIndex: Int = 0
    var mascotWidth: Int = 120
    var mascotHeight: Int = 120

    fun getImagePaths(): List<String> = listOf(imagePath1, imagePath2, imagePath3, imagePath4)

    fun getValidImagePaths(): List<String> = getImagePaths().filter { it.isNotBlank() }

    fun setImagePath(index: Int, path: String) {
        when (index) {
            0 -> imagePath1 = path
            1 -> imagePath2 = path
            2 -> imagePath3 = path
            3 -> imagePath4 = path
        }
    }

    override fun getState(): MascotSettingsState = this

    override fun loadState(state: MascotSettingsState) {
        this.imagePath1 = state.imagePath1
        this.imagePath2 = state.imagePath2
        this.imagePath3 = state.imagePath3
        this.imagePath4 = state.imagePath4
        this.currentIndex = state.currentIndex
        this.mascotWidth = state.mascotWidth
        this.mascotHeight = state.mascotHeight
    }

    companion object {
        val instance: MascotSettingsState
            get() = service()
    }
}