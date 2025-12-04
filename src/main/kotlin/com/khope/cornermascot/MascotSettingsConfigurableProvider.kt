package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import javax.swing.JComponent

class MascotSettingsConfigurableProvider : ConfigurableProvider() {
    override fun createConfigurable(): Configurable {
        return MascotSettingsConfigurable()
    }
}