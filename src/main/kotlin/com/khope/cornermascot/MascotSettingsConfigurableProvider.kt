package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider

class MascotSettingsConfigurableProvider : ConfigurableProvider() {
    override fun createConfigurable(): Configurable {
        return MascotSettingsConfigurable()
    }
}