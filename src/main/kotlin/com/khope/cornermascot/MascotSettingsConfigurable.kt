package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class MascotSettingsConfigurable : Configurable {

    private lateinit var panel: JPanel
    private lateinit var pathField: JTextField
    private lateinit var browseButton: JButton
    private lateinit var descriptionLabel: JLabel

    override fun getDisplayName(): String = "Corner Mascot"

    override fun createComponent(): JComponent {
        panel = JPanel()

        descriptionLabel = JLabel(
            """
            <html>
            <body>
                <b>Select a local image file to use as the mascot.</b><br>
                Supported: PNG / JPG<br>
                After selecting an image, click <b>Apply</b> to update immediately.
            </body>
            </html>
            """.trimIndent()
        )

        pathField = JTextField(30)
        browseButton = JButton("Browse")

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

        panel.layout = java.awt.BorderLayout()

        val top = JPanel()
        top.add(pathField)
        top.add(browseButton)

        panel.add(descriptionLabel, java.awt.BorderLayout.NORTH)
        panel.add(top, java.awt.BorderLayout.CENTER)

        return panel
    }

    override fun isModified(): Boolean {
        val settings = MascotSettingsState.instance
        return settings.imagePath != pathField.text
    }

    override fun apply() {
        MascotSettingsState.instance.imagePath = pathField.text
        CornerMascotStartupActivity.reloadMascot()
    }
}
