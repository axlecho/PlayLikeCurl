package karacken.curl

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PageRenderer(private val context: Context) : Renderer {

    private val frontPage: Page

    init {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        frontPage = Page(size.x)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f)
        gl.glClearDepthf(1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
    }


    override fun onDrawFrame(gl: GL10) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()

//        gl.glPushMatrix()
//        gl.glTranslatef(0.0f, 0.0f, -2.0f)
//        gl.glTranslatef(-0.5f, -0.5f, 0.0f)
//        leftPage.draw(gl, context)
//        gl.glPopMatrix()

        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, -2.0f)
        gl.glTranslatef(-0.5f, -0.5f, 0.0f)
        frontPage.draw(gl)
        gl.glPopMatrix()

//        gl.glPushMatrix()
//        gl.glTranslatef(0.0f, 0.0f, -2.0f)
//        gl.glTranslatef(-0.5f, -0.5f, 0.0f)
//        rightPage.draw(gl, context)
//        gl.glPopMatrix()

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        var height = height
        if (height == 0) {
            height = 1
        }

        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        if (height > width)
            GLU.gluPerspective(gl, 45.0f, width.toFloat() / height.toFloat(), 0.1f, 100.0f)
        else
            GLU.gluPerspective(gl, 45.0f, height.toFloat() / width.toFloat(), 0.1f, 100.0f)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    fun updatePageRes(resource: PageResource) {
        frontPage.updateRes(context.assets.open(resource.data))
    }

    fun resetPages() {
        frontPage.setPercent(0.5f)
    }

    fun setPercent(percent: Float) {
        frontPage.setPercent(percent)
    }
}