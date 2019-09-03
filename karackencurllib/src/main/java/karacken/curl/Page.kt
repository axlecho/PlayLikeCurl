package karacken.curl

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

import javax.microedition.khronos.opengles.GL10

/**
 * Created by karacken on 18/11/16.
 */
open class Page(screen_width: Int) {

    val RADIUS = 0.18f
    var curlCirclePosition = 25f
    internal var bitmap_ratio = 1.0f
    internal var screen_width = 0

    private var isactive = false
    var vertexBuffer: FloatBuffer? = null
    private val textureBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    var res_id = ""
        set(res_id) {
            field = res_id
            needtextureupdate = true
        }
    private var needtextureupdate = false

    private val textures = IntArray(1)
    var vertices = FloatArray((GRID + 1) * (GRID + 1) * 3)
    private val texture = FloatArray((GRID + 1) * (GRID + 1) * 2)
    private val indices = ShortArray(GRID * GRID * 6)

    internal var h_w_ratio: Float = 0.toFloat()
    internal var h_w_correction: Float = 0.toFloat()
    fun isactive(): Boolean {
        return isactive
    }


    fun setIsactive(isactive: Boolean) {
        this.isactive = isactive
    }

    open fun calculateVerticesCoords() {
        h_w_ratio = bitmap_ratio
        h_w_correction = (h_w_ratio - 1f) / 2.0f
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

    fun draw(gl: GL10, context: Context) {
        if (needtextureupdate) {
            needtextureupdate = false
            loadGLTexture(gl, context)
        }
        calculateVerticesCoords()
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glFrontFace(GL10.GL_CCW)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)
        // gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
    }

    fun loadInputStreamToGLTexture(gl: GL10, inputStream: InputStream) {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        loadBitmapToGLTexture(gl, bitmap)
        bitmap.recycle()
    }

    fun loadBitmapToGLTexture(gl: GL10, bitmap: Bitmap) {
        gl.glGenTextures(1, textures, 0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        //bitmap_ratio = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        //    bitmap.height.toFloat() / bitmap.width.toFloat()
        //else
        //    bitmap.width.toFloat() / bitmap.height.toFloat()


    }

    fun loadGLTexture(gl: GL10, context: Context) {
        if (this.res_id.isEmpty()) return
        val inputStream = context.assets.open(this.res_id)
        loadInputStreamToGLTexture(gl, inputStream)
    }

    companion object {
        val GRID = 25
    }
}