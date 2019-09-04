package karacken.curl

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU
import android.view.Display

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by karacken on 18/11/16.
 */
class PageRenderer(private val context: Context) : Renderer {

    var leftPage: Page
    var frontPage: Page
    var rightPage: Page
    var active_page: PAGE? = null

    val currentPagePerc: Int
        get() {
            return when (active_page) {
                PageRenderer.PAGE.LEFT -> (leftPage.curlCirclePosition / Page.GRID * 100).toInt()
                PageRenderer.PAGE.RIGHT -> (rightPage.curlCirclePosition / Page.GRID * 100).toInt()
                PageRenderer.PAGE.CURRENT -> (frontPage.curlCirclePosition / Page.GRID * 100).toInt()
                else -> (frontPage.curlCirclePosition / Page.GRID * 100).toInt()
            }
        }
    val currentPageValue: Float
        get() {
            return when (active_page) {
                PageRenderer.PAGE.LEFT -> leftPage.curlCirclePosition
                PageRenderer.PAGE.RIGHT -> rightPage.curlCirclePosition
                PageRenderer.PAGE.CURRENT -> frontPage.curlCirclePosition
                else -> frontPage.curlCirclePosition
            }


        }


    enum class PAGE {
        LEFT, RIGHT, CURRENT
    }

    init {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        leftPage = PageLeft(width)
        frontPage = PageFront(width)
        rightPage = PageRight(width)


        togglePageActive(PAGE.CURRENT)
        leftPage.curlCirclePosition = Page.GRID * (PAGE_RGHT.toFloat() / 100f)
        rightPage.curlCirclePosition = Page.GRID.toFloat()


    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        frontPage.loadGLTexture(gl, this.context)
        rightPage.loadGLTexture(gl, this.context)
        leftPage.loadGLTexture(gl, this.context)


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
        frontPage.draw(gl, context)
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

    fun updatePageRes(lef_res: String, front_res: String, right_res: String) {
        leftPage.res_id = lef_res
        frontPage.res_id = front_res
        rightPage.res_id = right_res

    }

    fun togglePageActive(page: PAGE) {

        if (active_page == null || active_page != page) {
            active_page = page

            when (active_page) {
                PageRenderer.PAGE.LEFT -> {
                }
                PageRenderer.PAGE.RIGHT -> {
                }
                PageRenderer.PAGE.CURRENT -> {
                }
            }

        }

    }

    fun updateCurlPosition(value: Float) {
        when (active_page) {
            PageRenderer.PAGE.LEFT -> leftPage.curlCirclePosition = value
            PageRenderer.PAGE.RIGHT -> rightPage.curlCirclePosition = value
            PageRenderer.PAGE.CURRENT -> frontPage.curlCirclePosition = value
        }
    }

    fun resetPages() {
        leftPage.curlCirclePosition = Page.GRID * (PAGE_RGHT.toFloat() / 100f)
        rightPage.curlCirclePosition = Page.GRID.toFloat()
        frontPage.curlCirclePosition = Page.GRID.toFloat()
        togglePageActive(PAGE.CURRENT)

    }

    companion object {

        val PAGE_LEFT = 100
        val PAGE_RGHT = -5
    }


}