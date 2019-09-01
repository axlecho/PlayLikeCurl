package karacken.curl

import android.arch.lifecycle.Observer
import android.opengl.GLSurfaceView
import android.support.v4.app.FragmentActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

class PageSurfaceView(context: FragmentActivity, private val model: PageModel) : GLSurfaceView(context), GestureDetector.OnGestureListener {

    private val mGesturedDetector: GestureDetector = GestureDetector(context, this)

    var onPageChangeListener: OnPageChangeListener? = null

    private var canSwipeLeft = false
    private var canSwipeRight = false

    private val resObserver = Observer<List<String>> { res ->
        // Update the UI, in this case, a TextView.
    }

    private val indexOberver = Observer<Int> { index ->
        if (index == null) {
            return@Observer
        }

        this.model.res.value?.let { list ->
            when (index) {
                0 -> {
                    renderer.updatePageRes(list[index], list[index], list[index + 1])
                    canSwipeLeft = false
                    canSwipeRight = true
                }
                list.size - 1 -> {
                    renderer.updatePageRes(list[index - 1], list[index], list[index])
                    canSwipeLeft = true
                    canSwipeRight = false
                }
                else -> {
                    renderer.updatePageRes(list[index - 1], list[index], list[index + 1])
                    canSwipeLeft = true
                    canSwipeRight = true
                }
            }
        }
    }

    private var x1 = 0f
    private var pos = 0f
    private val renderer: PageRenderer = PageRenderer(context)

    init {
        setRenderer(renderer)
        model.res.observe(context, resObserver)
        model.currentPosition.observe(context, indexOberver)
    }


    fun onPageTouchEvent(event: MotionEvent): Boolean {

        if (mGesturedDetector.onTouchEvent(event))
            return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                renderer.togglePageActive(PageRenderer.PAGE.CURRENT)
                pos = renderer.currentPageValue
            }
            MotionEvent.ACTION_MOVE -> {
                val perc_move = (event.x - x1) / width.toFloat()
                if (event.x - x1 > 0) { // page_left
                    if (pos >= Page.GRID && canSwipeLeft) {
                        renderer.togglePageActive(PageRenderer.PAGE.LEFT)
                        pos = renderer.currentPageValue
                    }
                    val value = pos + perc_move * Page.GRID
                    if (value <= Page.GRID)
                        renderer.updateCurlPosition(value)
                } else if (event.x - x1 < 0) {//  page_right
                    val value = (1 - Math.abs(perc_move)) * Page.GRID - (Page.GRID - pos)
                    if (canSwipeRight)
                        renderer.updateCurlPosition(value)
                }
            }
            MotionEvent.ACTION_UP ->
                if (renderer.active_page == PageRenderer.PAGE.CURRENT)
                    animatePagetoDefault(PageRenderer.PAGE_LEFT, false, AccelerateDecelerateInterpolator())
                else
                    animatePagetoDefault(PageRenderer.PAGE_RGHT, false, AccelerateDecelerateInterpolator())
        }
        return super.onTouchEvent(event)
    }


    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
            return false
        } else {
            if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                return false
            }
            if (e1.x - e2.x > SWIPE_MIN_DISTANCE && canSwipeRight) {
                animatePagetoDefault(PageRenderer.PAGE_RGHT, true, DecelerateInterpolator())
                return true
            } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && canSwipeLeft) {
                animatePagetoDefault(PageRenderer.PAGE_LEFT, true, DecelerateInterpolator())
                return true
            }
        }

        return false
    }

    private fun animatePagetoDefault(end_perc: Int, ispagechanged: Boolean, interpolator: Interpolator) {
        val start_per = renderer.currentPagePerc
        if (start_per == end_perc) {
            renderer.resetPages()
            if (ispagechanged) {
                processPageChange(end_perc)
            }

            return
        }

        val animateCounter = AnimateCounter.Builder()
                .setCount(start_per, end_perc)
                .setDuration(300)
                .setInterpolator(interpolator)
                .build()
        animateCounter.setAnimateCounterListener(object : AnimateCounter.AnimateCounterListener {


            override fun onAnimateCounterEnd() {
                renderer.resetPages()
                if (ispagechanged) {
                    processPageChange(end_perc)
                }
            }

            override fun onValueUpdate(value: Float) {
                renderer.updateCurlPosition(Page.GRID * value / 100f)
            }
        })
        animateCounter.execute()
    }

    private fun processPageChange(page_type: Int) {
        model.currentPosition.value?.let {
            model.currentPosition.value = if (page_type == PageRenderer.PAGE_LEFT) it + 1 else it - 1
            onPageChangeListener?.onPageChanged(it)
        }
    }

    interface OnPageChangeListener {
        fun onPageChanged(position: Int)

    }

    companion object {
        internal const val SWIPE_MIN_DISTANCE = 120
        internal const val SWIPE_MAX_OFF_PATH = 250
        internal const val SWIPE_THRESHOLD_VELOCITY = 200
    }
}
