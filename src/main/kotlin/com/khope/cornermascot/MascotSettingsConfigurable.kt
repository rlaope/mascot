package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JTextField

class MascotSettingsConfigurable : Configurable {

    private val panel = JPanel()
    private val pathField = JTextField(30)
    private val browseButton = JButton("Browse")

    override fun getDisplayName(): String = "Corner Mascot"

    override fun createComponent(): JComponent {
        val settings = MascotSettingsState.instance
        pathField.text = settings.imagePath

        browseButton.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.FILES_ONLY
            val result = chooser.showOpenDialog(null)

            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                if (file.exists()) {
                    pathField.text = file.absolutePath
                }
            }
        }

        panel.add(pathField)
        panel.add(browseButton)
        return panel
    }

    override fun isModified(): Boolean {
        TODO("Not yet implemented")
    }

    override fun apply() {
        MascotSettingsState.instance.imagePath = pathField.text
    }
}