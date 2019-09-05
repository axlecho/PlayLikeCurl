package karacken.curl

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by karacken on 18/11/16.
 */
class PageFront(screen_width: Int) : Page(screen_width) {
    override fun calculateVerticesCoords() {

        super.calculateVerticesCoords()
        val angle = 1.0f / (GRID.toFloat() * RADIUS)

        // 计算每个顶点坐标
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 3 * (row * (GRID + 1) + col)
                // vertices[pos + 2] = depth
                // 横向位移比例
                val perc = 1.0f - curlCirclePosition / GRID.toFloat()
                // 横向位移
                val dx = GRID - curlCirclePosition
                // var calc_r = perc * RADIUS
                // if (calc_r > RADIUS)  calc_r = RADIUS
                // 水波半径
                var calc_r = RADIUS * 1
                var mov_x = 0f

                if (perc < 0.20f) calc_r = RADIUS * perc * 5f
                if (perc > 0.05f) mov_x = perc - 0.05f

                val w_h_ratio = 1 - calc_r

                vertices[pos] = col.toFloat() / GRID.toFloat() * w_h_ratio - mov_x                                      // x
                vertices[pos + 1] = row.toFloat() / GRID.toFloat() * h_w_ratio - h_w_correction                         // y
                vertices[pos + 2] = (calc_r * Math.sin(3.14 / (GRID * 0.60f) * (col - dx)) + calc_r * 1.1f).toFloat()   // z  Asin(2pi/wav*x)
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
    }

    companion object {
        private val depth = -0.002f
    }

}