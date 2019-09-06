package karacken.curl

import android.opengl.GLSurfaceView
import android.support.v4.app.FragmentActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import karacken.curl.utils.PLog

class PageSurfaceView(context: FragmentActivity, private val model: PageModel) : GLSurfaceView(context), GestureDetector.OnGestureListener {

    private val mGesturedDetector: GestureDetector = GestureDetector(context, this)


    private val animate = PageAnimate(context, model)

    fun goTo(index: Int) {
        this.model.res.value?.let { list ->
            PLog.v("[index] -> $index")
            when (index) {
                0 -> animate.updatePageRes(list[index], list[index], list[index + 1])
                list.size - 1 -> animate.updatePageRes(list[index - 1], list[index], list[index])
                else -> animate.updatePageRes(list[index - 1], list[index], list[index + 1])
            }
        }
    }

    init {
        this.setRenderer(animate.renderer)
    }


    fun onPageTouchEvent(event: MotionEvent): Boolean {

        if (mGesturedDetector.onTouchEvent(event))
            return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> animate.onDown(event.x)
            MotionEvent.ACTION_MOVE -> animate.onMove(event.x, this.width)
            MotionEvent.ACTION_UP -> animate.onUp(event.x)
        }
        return super.onTouchEvent(event)
    }


    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        animate.animatePagetoDefault(PageRenderer.PAGE_RGHT, false, AccelerateDecelerateInterpolator())
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return animate.onFling(e1, e2, velocityX, velocityY)
    }


    interface OnPageChangeListener {
        fun onPageChanged(position: Int)
    }


}
