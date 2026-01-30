package com.khope.cornermascot

import com.intellij.openapi.options.Configurable
import javax.swing.*
import java.awt.*

class MascotSettingsConfigurable : Configurable {

    private var panel: JPanel? = null

    private val pathFields = arrayOfNulls<JTextField>(4)
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
                <b>Select up to 4 local image files to use as the bottom-right mascot.</b><br>
                Supported formats: PNG / JPG.<br>
                Hover over the mascot and use arrows to switch between images.<br>
                After selecting or resizing the mascot, click Apply to update immediately.
            </html>
            """.trimIndent()
        )
        root.add(desc, c)

        val imagePaths = settings.getImagePaths()
        for (i in 0 until 4) {
            c.gridy++
            val pathRow = JPanel(FlowLayout(FlowLayout.LEFT))
            pathRow.add(JLabel("Image ${i + 1}:"))
            pathFields[i] = JTextField(imagePaths[i], 25)
            val browseBtn = JButton("Browse")
            val fieldIndex = i

            browseBtn.addActionListener {
                val chooser = JFileChooser()
                chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                val result = chooser.showOpenDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    chooser.selectedFile?.let { file ->
                        if (file.exists()) pathFields[fieldIndex]?.text = file.absolutePath
                    }
                }
            }
            pathRow.add(pathFields[i])
            pathRow.add(browseBtn)
            root.add(pathRow, c)
        }

        c.gridy++
        val widthRow = JPanel(FlowLayout(FlowLayout.LEFT))
        widthRow.add(JLabel("Mascot Width:"))
        widthField = JTextField(
            (if (settings.mascotWidth > 0) settings.mascotWidth else 120).toString(),
            5
        )
        widthRow.add(widthField)
        root.add(widthRow, c)

        c.gridy++
        val heightRow = JPanel(FlowLayout(FlowLayout.LEFT))
        heightRow.add(JLabel("Mascot Height:"))
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
        val imagePaths = settings.getImagePaths()

        for (i in 0 until 4) {
            if (imagePaths[i] != (pathFields[i]?.text ?: "")) return true
        }

        return settings.mascotWidth.toString() != widthField?.text ||
                settings.mascotHeight.toString() != heightField?.text
    }

    override fun apply() {
        val settings = MascotSettingsState.instance

        for (i in 0 until 4) {
            settings.setImagePath(i, pathFields[i]?.text ?: "")
        }

        settings.mascotWidth = widthField?.text?.toIntOrNull() ?: 120
        settings.mascotHeight = heightField?.text?.toIntOrNull() ?: 120

        // Reset current index if it's now invalid
        val validCount = settings.getValidImagePaths().size
        if (validCount > 0 && settings.currentIndex >= validCount) {
            settings.currentIndex = 0
        }

        SwingUtilities.invokeLater {
            CornerMascotStartupActivity.reloadMascot()
        }
    }
}
