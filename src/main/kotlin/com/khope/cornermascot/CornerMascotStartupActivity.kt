package com.khope.cornermascot

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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

            mascotComponent?.let {
                layered.remove(it)
                layered.revalidate()
                layered.repaint()
            }

            val settings = MascotSettingsState.instance
            val width = settings.mascotWidth
            val height = settings.mascotHeight

            val validPaths = settings.getValidImagePaths()
            val hasMultipleImages = validPaths.size > 1

            // Ensure currentIndex is valid
            if (validPaths.isNotEmpty() && settings.currentIndex >= validPaths.size) {
                settings.currentIndex = 0
            }

            val currentPath = if (validPaths.isNotEmpty()) validPaths[settings.currentIndex] else ""

            val rawImage: Image =
                if (currentPath.isNotBlank() && File(currentPath).exists()) {
                    ImageIcon(currentPath).image
                } else {
                    ImageIcon(CornerMascotStartupActivity::class.java.classLoader.getResource("icons/mascot.png")).image
                }

            val image = rawImage.getScaledInstance(width, height, Image.SCALE_SMOOTH)

            val arrowSize = 24
            val arrowPadding = 4
            val totalWidth = width + (if (hasMultipleImages) (arrowSize + arrowPadding) * 2 else 0)

            val newComponent = object : JComponent() {
                private var isHovered = false
                private var leftArrowHovered = false
                private var rightArrowHovered = false

                init {
                    addMouseListener(object : MouseAdapter() {
                        override fun mouseEntered(e: MouseEvent?) {
                            isHovered = true
                            repaint()
                        }

                        override fun mouseExited(e: MouseEvent?) {
                            isHovered = false
                            leftArrowHovered = false
                            rightArrowHovered = false
                            repaint()
                        }

                        override fun mouseClicked(e: MouseEvent?) {
                            if (!hasMultipleImages || e == null) return

                            val leftArrowBounds = getLeftArrowBounds()
                            val rightArrowBounds = getRightArrowBounds()

                            when {
                                leftArrowBounds.contains(e.point) -> {
                                    settings.currentIndex = if (settings.currentIndex > 0) {
                                        settings.currentIndex - 1
                                    } else {
                                        validPaths.size - 1
                                    }
                                    reloadMascot()
                                }
                                rightArrowBounds.contains(e.point) -> {
                                    settings.currentIndex = if (settings.currentIndex < validPaths.size - 1) {
                                        settings.currentIndex + 1
                                    } else {
                                        0
                                    }
                                    reloadMascot()
                                }
                            }
                        }
                    })

                    addMouseMotionListener(object : MouseAdapter() {
                        override fun mouseMoved(e: MouseEvent?) {
                            if (!hasMultipleImages || e == null) return

                            val prevLeftHovered = leftArrowHovered
                            val prevRightHovered = rightArrowHovered

                            leftArrowHovered = getLeftArrowBounds().contains(e.point)
                            rightArrowHovered = getRightArrowBounds().contains(e.point)

                            if (prevLeftHovered != leftArrowHovered || prevRightHovered != rightArrowHovered) {
                                cursor = if (leftArrowHovered || rightArrowHovered) {
                                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                } else {
                                    Cursor.getDefaultCursor()
                                }
                                repaint()
                            }
                        }
                    })
                }

                private fun getLeftArrowBounds(): Rectangle {
                    val y = (height - arrowSize) / 2
                    return Rectangle(0, y, arrowSize, arrowSize)
                }

                private fun getRightArrowBounds(): Rectangle {
                    val y = (height - arrowSize) / 2
                    return Rectangle(totalWidth - arrowSize, y, arrowSize, arrowSize)
                }

                override fun getPreferredSize(): Dimension = Dimension(totalWidth, height)

                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    val g2d = g as Graphics2D
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                    val imageX = if (hasMultipleImages) arrowSize + arrowPadding else 0
                    g2d.drawImage(image, imageX, 0, width, height, this)

                    if (hasMultipleImages && isHovered) {
                        drawArrow(g2d, getLeftArrowBounds(), true, leftArrowHovered)
                        drawArrow(g2d, getRightArrowBounds(), false, rightArrowHovered)
                    }
                }

                private fun drawArrow(g2d: Graphics2D, bounds: Rectangle, isLeft: Boolean, isHoveredArrow: Boolean) {
                    val centerX = bounds.x + bounds.width / 2
                    val centerY = bounds.y + bounds.height / 2
                    val arrowWidth = 8
                    val arrowHeight = 14

                    g2d.color = if (isHoveredArrow) {
                        Color(255, 255, 255, 230)
                    } else {
                        Color(255, 255, 255, 150)
                    }

                    g2d.fillOval(bounds.x, bounds.y, bounds.width, bounds.height)

                    g2d.color = if (isHoveredArrow) {
                        Color(50, 50, 50)
                    } else {
                        Color(100, 100, 100)
                    }

                    val xPoints: IntArray
                    val yPoints: IntArray

                    if (isLeft) {
                        xPoints = intArrayOf(centerX + arrowWidth / 2, centerX - arrowWidth / 2, centerX + arrowWidth / 2)
                        yPoints = intArrayOf(centerY - arrowHeight / 2, centerY, centerY + arrowHeight / 2)
                    } else {
                        xPoints = intArrayOf(centerX - arrowWidth / 2, centerX + arrowWidth / 2, centerX - arrowWidth / 2)
                        yPoints = intArrayOf(centerY - arrowHeight / 2, centerY, centerY + arrowHeight / 2)
                    }

                    g2d.fillPolygon(xPoints, yPoints, 3)
                }
            }

            mascotComponent = newComponent
            layered.add(newComponent, JLayeredPane.PALETTE_LAYER)

            fun reposition() {
                val size = layered.size
                val x = size.width - totalWidth - 30
                val y = size.height - height - 40
                newComponent.setBounds(x, y, totalWidth, height)
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
