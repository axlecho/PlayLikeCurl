package karacken.curl

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by karacken on 18/11/16.
 */
class PageRight(screen_width: Int) : Page(screen_width) {
    override fun calculateVerticesCoords() {
        super.calculateVerticesCoords()
        val angle = 1.0f / (Page.GRID.toFloat() * RADIUS)
        for (row in 0..Page.GRID)
            for (col in 0..Page.GRID) {
                val pos = 3 * (row * (Page.GRID + 1) + col)

                    vertices[pos + 2] = depth

                vertices[pos] = col.toFloat() / Page.GRID.toFloat()


                vertices[pos + 1] = row.toFloat() / Page.GRID.toFloat() * h_w_ratio - h_w_correction
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer!!.put(vertices)
        vertexBuffer!!.position(0)

    }

    companion object {
        private val depth = -0.003f
    }

}