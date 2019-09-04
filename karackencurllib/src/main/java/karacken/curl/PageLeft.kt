package karacken.curl

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by karacken on 18/11/16.
 */
class PageLeft(screen_width: Int) : Page(screen_width) {

    override fun calculateVerticesCoords() {

        super.calculateVerticesCoords()
        val angle = 1.0f / (Page.GRID.toFloat() * RADIUS)
        for (row in 0..Page.GRID)
            for (col in 0..Page.GRID) {
                val pos = 3 * (row * (Page.GRID + 1) + col)

                vertices[pos + 2] = depth

                var perc = 1.0f - curlCirclePosition / Page.GRID.toFloat()
                perc *= 0.75f

                val dx = Page.GRID - curlCirclePosition
                var calc_r = perc * RADIUS
                if (calc_r > RADIUS)
                    calc_r = RADIUS

                calc_r = RADIUS * 1
                var mov_x = 0f
                if (perc < 0.20f)
                    calc_r = RADIUS * perc * 5f

                mov_x = perc



                    vertices[pos + 2] = (calc_r * Math.sin(3.14 / (Page.GRID * 0.50f) * (col - dx)) + calc_r * 1.1f).toFloat() //Asin(2pi/wav*x)
                val w_h_ratio = 1 - calc_r
                vertices[pos] = col.toFloat() / Page.GRID.toFloat() * w_h_ratio - mov_x


                vertices[pos + 1] = row.toFloat() / Page.GRID.toFloat() * h_w_ratio - h_w_correction
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer!!.put(vertices)
        vertexBuffer!!.position(0)

    }

    companion object {


        private val depth = -0.001f
    }

}