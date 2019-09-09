package karacken.curl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import karacken.curl.utils.PLog
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min


open class Page(screen_width: Int) {
    companion object {
        const val RADIUS = 0.18f              // 波浪半径
        const val GRID = 25                   // 网格
    }

    private var curlCirclePosition = GRID.toFloat()

    private var bitmap_ratio = 1.0f         // 图片比例
    private var screen_width = 0            // 视频宽度

    private var vertexBuffer: FloatBuffer? = null
    private val textureBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer


    private val textures = IntArray(1)
    private var vertices = FloatArray((GRID + 1) * (GRID + 1) * 3)              // 网格向量
    private val texture = FloatArray((GRID + 1) * (GRID + 1) * 2)               // 纹理
    private val indices = ShortArray(GRID * GRID * 6)

    private var hwRatio: Float = 0.0f
    private var hwCorrection: Float = 0.0f
    private var bitmap: Bitmap? = null

    init {
        this.screen_width = screen_width
        calculateVerticesCoords()
        calculateFacesCoords()
        calculateTextureCoords()

        var byteBuf = ByteBuffer.allocateDirect(texture.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        textureBuffer = byteBuf.asFloatBuffer()
        textureBuffer.put(texture)
        textureBuffer.position(0)

        byteBuf = ByteBuffer.allocateDirect(indices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        indexBuffer = byteBuf.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

    }

    fun draw(gl: GL10) {
        bitmap?.let {
            loadBitmapToGLTexture(gl, it)
            it.recycle()
            bitmap = null
        }

        calculateVerticesCoords()
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glFrontFace(GL10.GL_CCW)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
    }

    fun updateRes(inputStream: InputStream) {
        bitmap = BitmapFactory.decodeStream(inputStream)
    }

    fun setPercent(percent: Float) {
        curlCirclePosition = GRID * min(1.0f, max(percent, 0.0f))
        PLog.v("setPercent curlCirclePosition $curlCirclePosition")
    }

    private fun loadBitmapToGLTexture(gl: GL10, bitmap: Bitmap) {
        gl.glGenTextures(1, textures, 0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap_ratio = bitmap.height.toFloat() / bitmap.width.toFloat()
        //bitmap_ratio = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        //    bitmap.height.toFloat() / bitmap.width.toFloat()
        //else
        //    bitmap.width.toFloat() / bitmap.height.toFloat()
    }

    private fun calculateVerticesCoords() {
        hwRatio = bitmap_ratio
        hwCorrection = (hwRatio - 1f) / 2.0f
        calculateVerticesCoordsFront()
    }

    private fun calculateVerticesCoordsFront() {

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
                vertices[pos + 1] = row.toFloat() / GRID.toFloat() * hwRatio - hwCorrection                         // y
                vertices[pos + 2] = (calc_r * Math.sin(3.14 / (GRID * 0.60f) * (col - dx)) + calc_r * 1.1f).toFloat()   // z  Asin(2pi/wav*x)
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
    }


    private fun calculateVerticesCoordsRight() {
        val angle = 1.0f / (GRID.toFloat() * RADIUS)
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 3 * (row * (GRID + 1) + col)
                vertices[pos + 2] = -0.003f
                vertices[pos] = col.toFloat() / GRID.toFloat()


                vertices[pos + 1] = row.toFloat() / GRID.toFloat() * hwRatio - hwCorrection
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)

    }

    private fun calculateVerticesCoordsLeft() {
        val angle = 1.0f / (GRID.toFloat() * RADIUS)
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 3 * (row * (GRID + 1) + col)
                vertices[pos + 2] = -0.001f

                var perc = 1.0f - curlCirclePosition / GRID.toFloat()
                perc *= 0.75f

                val dx = GRID - curlCirclePosition
                var calc_r = perc * RADIUS
                if (calc_r > RADIUS)
                    calc_r = RADIUS

                calc_r = RADIUS * 1
                var mov_x = 0f
                if (perc < 0.20f)
                    calc_r = RADIUS * perc * 5

                mov_x = perc



                vertices[pos + 2] = (calc_r * Math.sin(3.14 / (GRID * 0.50f) * (col - dx)) + calc_r * 1.1f).toFloat() //Asin(2pi/wav*x)
                val w_h_ratio = 1 - calc_r
                vertices[pos] = col.toFloat() / GRID.toFloat() * w_h_ratio - mov_x


                vertices[pos + 1] = row.toFloat() / GRID.toFloat() * hwRatio - hwCorrection
            }

        val byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuf.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
    }

    private fun calculateFacesCoords() {
        for (row in 0 until GRID)
            for (col in 0 until GRID) {
                val pos = 6 * (row * GRID + col)

                indices[pos] = (row * (GRID + 1) + col).toShort()
                indices[pos + 1] = (row * (GRID + 1) + col + 1).toShort()
                indices[pos + 2] = ((row + 1) * (GRID + 1) + col).toShort()

                indices[pos + 3] = (row * (GRID + 1) + col + 1).toShort()
                indices[pos + 4] = ((row + 1) * (GRID + 1) + col + 1).toShort()
                indices[pos + 5] = ((row + 1) * (GRID + 1) + col).toShort()

            }
    }

    private fun calculateTextureCoords() {
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 2 * (row * (GRID + 1) + col)
                texture[pos] = col / GRID.toFloat()
                texture[pos + 1] = 1 - row / GRID.toFloat()
            }
    }
}