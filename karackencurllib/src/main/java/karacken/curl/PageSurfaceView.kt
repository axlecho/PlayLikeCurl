package karacken.curl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import karacken.curl.utils.PLog

class PageSurfaceView(context: Context, private val model: PageModel) : GLSurfaceView(context), GestureDetector.OnGestureListener {

    private val mGesturedDetector: GestureDetector = GestureDetector(context, this)
    private val animate = PageAnimate(context)

    init {
        this.setRenderer(animate.renderer)
        model.imageIndex.observeForever { index ->
            PLog.v("index: $index")
            index?.let {
                model.imageResources.value?.let {
                    animate.animatePagetoDefault(it[index],it[index + 1])
                }
            }

            // if (model.imageIndex.isChanged()) {

            // }
        }

        model.status.observeForever {

        }

        model.position.observeForever {

        }

        model.imageResources.observeForever {

        }
    }


    fun onPageTouchEvent(event: MotionEvent): Boolean {

        if (mGesturedDetector.onTouchEvent(event))
            return true

        // when (event.action) {
        //    MotionEvent.ACTION_DOWN -> animate.onDown(event.x)
        //     MotionEvent.ACTION_MOVE -> animate.onMove(event.x, this.width)
        //    MotionEvent.ACTION_UP -> animate.onUp(event.x)
        // }
        return super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        model.imageGoNext()
        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (Math.abs(e1.y - e2.y) > PageAnimate.SWIPE_MAX_OFF_PATH) {
            return false
        }

        if (Math.abs(velocityX) < PageAnimate.SWIPE_THRESHOLD_VELOCITY) {
            return false
        }

        // fling to right
        if (e1.x - e2.x > PageAnimate.SWIPE_MIN_DISTANCE) {
            model.imageGoNext()
            return true
        }

        // if (e2.x - e1.x > PageAnimate.SWIPE_MIN_DISTANCE) {
        //    model.imageGoPrevious()
        // }
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

}
