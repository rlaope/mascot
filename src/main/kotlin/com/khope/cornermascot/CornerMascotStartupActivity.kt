package com.khope.cornermascot

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.Image
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLayeredPane
import javax.swing.SwingUtilities
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

class CornerMascotStartupActivity : StartupActivity {

    companion object {
        private var mascotComponent: JComponent? = null

        fun reloadMascot() {
            mascotComponent?.let { comp ->
                val parent = comp.parent
                parent?.remove(comp)
                parent?.repaint()
            }
        }
    }

    override fun runActivity(project: Project) {
        EventQueue.invokeLater {
            val frame = WindowManager.getInstance().getFrame(project) ?: return@invokeLater
            val root = frame.rootPane ?: return@invokeLater
            val layered = root.layeredPane ?: return@invokeLater

            val settings = MascotSettingsState.instance

            val rawImage: Image =
                if (settings.imagePath.isNotBlank() && File(settings.imagePath).exists()) {
                    ImageIcon(settings.imagePath).image
                } else {
                    ImageIcon(javaClass.classLoader.getResource("icons/mascot.png")).image
                }

            val fixedWidth = 120
            val fixedHeight = 120

            val image = rawImage.getScaledInstance(fixedWidth, fixedHeight, Image.SCALE_SMOOTH)

            val component = object : JComponent() {
                override fun getPreferredSize(): Dimension =
                    Dimension(fixedWidth, fixedHeight)

                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    g.drawImage(image, 0, 0, width, height, this)
                }
            }

            layered.add(component, JLayeredPane.PALETTE_LAYER)

            fun updatePosition() {
                val size = layered.size
                val x = size.width - fixedWidth - 30
                val y = size.height - fixedHeight - 40
                component.setBounds(x, y, fixedWidth, fixedHeight)
                component.revalidate()
                component.repaint()
            }

            updatePosition()

            frame.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    SwingUtilities.invokeLater { updatePosition() }
                }
            })
        }
    }
}