import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.math.floor

data class Chemicals(val a: Double, val b: Double)
data class Dimensions(val height: Int, val width: Int)
data class Coordinate(val x: Int, val y: Int)


class ReactionDiffusion(val height: Int, val width: Int) {
    private val dimensions = Dimensions(height, width)
    var curr = mutableMapOf<Coordinate, Chemicals>()
    private var next = mutableMapOf<Coordinate, Chemicals>()

    private val feed = 0.055
    private val da = 1.0
    private val db = 0.5
    private val k  = 0.062
    private val t = 1

    private val weights = mapOf(Pair(0,0) to -1.0,
        Pair(-1,0) to .2,
        Pair(1,0) to .2,
        Pair(0,1) to .2,
        Pair(0,-1) to .2,

        Pair(1,1) to .05,
        Pair(-1,-1) to .05,
        Pair(1,-1) to .05,
        Pair(-1,1) to .05,
    )

    init {
        for (x in 0..dimensions.width) {
            for (y in 0 .. dimensions.height ) {
                curr[Coordinate(x,y)] = Chemicals(1.0, .0)
                next[Coordinate(x,y)] = Chemicals(1.0, .0)
            }
        }
    }

    fun addChemicals(coordinates: List<Coordinate>) {
        for (coordinate in coordinates) {
            curr[coordinate] = Chemicals(curr.getValue(coordinate).a, 1.0)
        }
    }

    fun tick() {
        for (item in curr) {
            val a = item.value.a
            val b = item.value.b
            val x = item.key.x
            val y = item.key.y
            val aPrime = (a + da * laplace(x, y, 'a') - a * b * b + feed * (1 - a))
            val bPrime = (b + db * laplace(x, y, 'b') + a * b * b - (k + feed) * b)
            next[item.key] = Chemicals(aPrime, bPrime)
        }
        val temp = curr
        curr = next
        next = temp
    }

    fun laplace(x: Int, y: Int, chem: Char): Double {
        var result = 0.0

        for (shift in weights) {
            val coordinate = Coordinate(x + shift.key.first, y + shift.key.second)
            if (coordinate !in curr) {
                return 0.0
            }
            if (chem.equals('a')) {
                result += curr.getValue(coordinate).a * shift.value
            } else {
                result += curr.getValue(coordinate).b * shift.value
            }
        }
        return result;
    }
}

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        val simulation = ReactionDiffusion(height, width)
        var coordinates = mutableListOf<Coordinate>()

        for (i in 0..50) {
            coordinates.add(Coordinate(0, i))
            coordinates.add(Coordinate(10, i))
            coordinates.add(Coordinate(15, i))
            coordinates.add(Coordinate(20, i))
            coordinates.add(Coordinate(25, i))
            coordinates.add(Coordinate(30, i))
        }
        simulation.addChemicals(coordinates)

        extend {
            simulation.tick()
            for (item in simulation.curr) {
                val coord = item.key
                val chemicals = item.value
                val r = floor( chemicals.a * 255.0)
                val g = floor((chemicals.a - chemicals.b) * 255.0)
                val b = floor(chemicals.b * 255.0)
                drawer.fill = ColorRGBa(r, g, b, 255.0)
                drawer.stroke = ColorRGBa(r, g, b, 255.0)
                drawer.rectangle(coord.x.toDouble() * 4, coord.y.toDouble() * 4, 4.0, 4.0)

            }
        }
    }
}
