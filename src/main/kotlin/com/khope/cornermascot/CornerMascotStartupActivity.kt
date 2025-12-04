package com.khope.cornermascot

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import javax.swing.*

class CornerMascotStartupActivity : StartupActivity {

    companion object {
        private var mascotComponent: JComponent? = null
        private var layeredPane: JLayeredPane? = null
        private var frameRef: JFrame? = null

        fun reloadMascot() {
            val layered = layeredPane ?: return
            val frame = frameRef ?: return

            mascotComponent?.let { layered.remove(it) }

            val settings = MascotSettingsState.instance

            val rawImage: Image =
                if (settings.imagePath.isNotBlank() && File(settings.imagePath).exists()) {
                    ImageIcon(settings.imagePath).image
                } else {
                    ImageIcon(CornerMascotStartupActivity::class.java.classLoader.getResource("icons/mascot.png")).image
                }

            val fixedWidth = 120
            val fixedHeight = 120
            val image = rawImage.getScaledInstance(fixedWidth, fixedHeight, Image.SCALE_SMOOTH)

            val newComponent = object : JComponent() {
                override fun getPreferredSize(): Dimension = Dimension(fixedWidth, fixedHeight)
                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    g.drawImage(image, 0, 0, width, height, this)
                }
            }

            mascotComponent = newComponent
            layered.add(newComponent, JLayeredPane.PALETTE_LAYER)

            fun reposition() {
                val size = layered.size
                val x = size.width - fixedWidth - 30
                val y = size.height - fixedHeight - 40
                newComponent.setBounds(x, y, fixedWidth, fixedHeight)
                newComponent.revalidate()
                newComponent.repaint()
            }

            reposition()

            frame.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    SwingUtilities.invokeLater { reposition() }
                }
            })

            layered.revalidate()
            layered.repaint()
        }
    }

    override fun runActivity(project: Project) {
        EventQueue.invokeLater {
            val frame = WindowManager.getInstance().getFrame(project) ?: return@invokeLater
            val root = frame.rootPane ?: return@invokeLater
            val layered = root.layeredPane ?: return@invokeLater

            frameRef = frame
            layeredPane = layered

            reloadMascot()
        }
    }
}
