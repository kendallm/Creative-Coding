
import org.openrndr.application
import org.openrndr.extra.gui.GUI
import org.openrndr.shape.Circle
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.olive.oliveProgram


fun main() = application {
    configure {
        width = 900
        height = 900
    }

    oliveProgram {

        val reactionDiffusionSystem = ReactionDiffusionSystem(width, height).apply {
            iterations = 20
            setOutputColors(listOf(ColorRGBa.WHITE, ColorRGBa.RED, ColorRGBa.BLUE, ColorRGBa.BLACK))

            // coral
//             feed = 0.0545
//             kill = 0.062

            // mitosis
             feed = 0.0367
             kill = 0.0649

            // worms
//             feed = 0.039
//             kill = 0.062
        }

        mouse.buttonUp.listen {
            reactionDiffusionSystem.addReagent(drawer) {
                circle(Circle(mouse.position, 5.0))
            }
        }

        keyboard.keyUp.listen {
            if (it.name == "c") {
                reactionDiffusionSystem.clear()
            }
        }

        extend(GUI()) {
            add(reactionDiffusionSystem)
        }

        extend {
            reactionDiffusionSystem.update(drawer)
            reactionDiffusionSystem.render(drawer)
        }
    }
}
