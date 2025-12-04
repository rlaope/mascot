package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import javax.swing.*
import java.awt.*

class MascotSettingsConfigurable : Configurable {

    private var panel: JPanel? = null

    private var pathField: JTextField? = null
    private var widthField: JTextField? = null
    private var heightField: JTextField? = null

    override fun getDisplayName(): String = "Corner Mascot"

    override fun createComponent(): JComponent {
        val settings = MascotSettingsState.instance

        val root = JPanel(GridBagLayout())
        val c = GridBagConstraints()

        c.insets = Insets(8, 8, 8, 8)
        c.anchor = GridBagConstraints.WEST
        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.gridx = 0
        c.gridy = 0

        val desc = JLabel(
            """
            <html>
                <b>Select a local image file to use as the bottom-right mascot.</b><br>
                Supported formats: PNG / JPG.<br>
                After selecting or resizing the mascot, click Apply to update immediately.
            </html>
            """.trimIndent()
        )
        root.add(desc, c)

        c.gridy++
        val pathRow = JPanel()
        pathField = JTextField(settings.imagePath ?: "", 30)
        val browseBtn = JButton("Browse")

        browseBtn.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.FILES_ONLY
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile?.let { file ->
                    if (file.exists()) pathField!!.text = file.absolutePath
                }
            }
        }
        pathRow.add(pathField)
        pathRow.add(browseBtn)
        root.add(pathRow, c)

        c.gridy++
        val widthRow = JPanel()
        widthRow.add(JLabel("Mascot Width"))
        widthField = JTextField(
            (if (settings.mascotWidth > 0) settings.mascotWidth else 120).toString(),
            5
        )
        widthRow.add(widthField)
        root.add(widthRow, c)

        c.gridy++
        val heightRow = JPanel()
        heightRow.add(JLabel("Mascot Height"))
        heightField = JTextField(
            (if (settings.mascotHeight > 0) settings.mascotHeight else 120).toString(),
            5
        )
        heightRow.add(heightField)
        root.add(heightRow, c)

        panel = root
        return root
    }

    override fun isModified(): Boolean {
        val settings = MascotSettingsState.instance

        return settings.imagePath != pathField?.text ||
                settings.mascotWidth.toString() != widthField?.text ||
                settings.mascotHeight.toString() != heightField?.text
    }

    override fun apply() {
        val settings = MascotSettingsState.instance

        settings.imagePath = pathField?.text ?: ""

        settings.mascotWidth = widthField?.text?.toIntOrNull() ?: 120
        settings.mascotHeight = heightField?.text?.toIntOrNull() ?: 120

        SwingUtilities.invokeLater {
            CornerMascotStartupActivity.reloadMascot()
        }
    }
}
