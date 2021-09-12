import org.openrndr.application
import org.openrndr.draw.shadeStyle
import org.openrndr.extra.olive.oliveProgram

class ShaderTut {

}


fun main() = application {
    configure {
        width = 920
        height = width
    }
    oliveProgram {
        extend {
            drawer.shadeStyle = shadeStyle {
                fragmentTransform = """ 
                    vec2 pos = c_boundsPosition.xy;
                    pos -= p_mouse;
                    float dist = length(pos) * 10;
                    float tao = 6.28318530718;
                    float val = 0.5 + 0.5 * sin(tao * (dist + p_time));
                    x_fill.rgb = vec3(val);
                    
                    
                """.trimIndent()
                parameter("time", (frameCount % 60) / 60.0)
                parameter("mouse", mouse.position / drawer.bounds.dimensions)
//                println(frameCount)
            }

            drawer.rectangle(drawer.bounds)
        }
    }
}