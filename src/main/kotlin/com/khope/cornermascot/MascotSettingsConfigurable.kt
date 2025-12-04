package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import javax.swing.*

class MascotSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private var pathField: JTextField? = null
    private lateinit var descriptionLabel: JLabel

    override fun getDisplayName(): String = "Corner Mascot"

    override fun createComponent(): JComponent {
        val p = JPanel()
        p.layout = BoxLayout(p, BoxLayout.Y_AXIS)

        descriptionLabel = JLabel(
            "<html><body>" +
                    "<b>Select a local image file to use as the bottom-right mascot.</b><br>" +
                    "Supported formats: PNG / JPG.<br>" +
                    "After selecting an image, click Apply to update immediately." +
                    "</body></html>"
        )

        val field = JTextField(30)
        val btn = JButton("Browse")

        val settings = MascotSettingsState.instance
        field.text = settings.imagePath

        btn.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.FILES_ONLY
            val result = chooser.showOpenDialog(null)

            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                if (file.exists()) {
                    field.text = file.absolutePath
                }
            }
        }

        val row = JPanel()
        row.add(field)
        row.add(btn)

        p.add(descriptionLabel)
        p.add(Box.createVerticalStrut(10))
        p.add(row)

        panel = p
        pathField = field

        return p
    }

    override fun isModified(): Boolean {
        val settings = MascotSettingsState.instance
        return settings.imagePath != pathField?.text
    }

    override fun apply() {
        MascotSettingsState.instance.imagePath = pathField?.text ?: ""
        CornerMascotStartupActivity.reloadMascot()
    }
}