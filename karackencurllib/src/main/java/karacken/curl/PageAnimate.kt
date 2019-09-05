package karacken.curl

import android.content.Context
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import karacken.curl.page.Page
import karacken.curl.utils.PLog

class PageAnimate(context: Context, private val model: PageModel) {
    companion object {
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_MAX_OFF_PATH = 250
        private const val SWIPE_THRESHOLD_VELOCITY = 200
    }

    val renderer: PageRenderer = PageRenderer(context)
    private var x = 0.0f
    private var pos = 0.0f

    fun animatePagetoDefault(end_perc: Int, ispagechanged: Boolean, interpolator: Interpolator) {
        val start = renderer.currentPagePerc
        if (start == end_perc) {
            renderer.resetPages()
            if (ispagechanged) {
                processPageChange(end_perc)
            }
            return
        }

        val animateCounter = AnimateCounter.Builder()
                .setCount(start, end_perc)
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

    fun updatePageRes(lef_res: String, front_res: String, right_res: String) {
        renderer.updatePageRes(lef_res, front_res, right_res)
    }

    fun onDown(_x: Float) {
        this.x = _x
        renderer.togglePageActive(PageRenderer.PAGE.CURRENT)
        this.pos = renderer.currentPageValue
    }

    fun onMove(_x: Float, width: Int) {
        val distance = (_x - x) / width.toFloat()
        if (_x - x > 0) {  // page_left
            if (model.isFirst()) {
                return
            }

            if (pos >= Page.GRID) {
                renderer.togglePageActive(PageRenderer.PAGE.LEFT)
                pos = renderer.currentPageValue
            }
            val value = pos + distance * Page.GRID
            if (value <= Page.GRID)
                renderer.updateCurlPosition(value)
        } else if (_x - x < 0) {   //  page_right
            if (model.isLast()) {
                return
            }
            val value = (1 - Math.abs(distance)) * Page.GRID - (Page.GRID - pos)
            renderer.updateCurlPosition(value)
        }
    }

    fun onUp(_x: Float) {
        if (renderer.active_page == PageRenderer.PAGE.CURRENT) {
            animatePagetoDefault(PageRenderer.PAGE_LEFT, false, AccelerateDecelerateInterpolator())
        } else {
            animatePagetoDefault(PageRenderer.PAGE_RGHT, false, AccelerateDecelerateInterpolator())
        }
    }

    fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
            return false
        }

        if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
            return false
        }

        if (e1.x - e2.x > SWIPE_MIN_DISTANCE && !model.isLast()) {
            animatePagetoDefault(PageRenderer.PAGE_RGHT, true, DecelerateInterpolator())
            return true
        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && !model.isFirst()) {
            animatePagetoDefault(PageRenderer.PAGE_LEFT, true, DecelerateInterpolator())
            return true
        }


        return false
    }


    private fun processPageChange(page_type: Int) {
        model.currentPosition.value?.let {
            PLog.v(model.currentPosition.value.toString())
            model.currentPosition.value = if (page_type == PageRenderer.PAGE_LEFT) it - 1 else it + 1
        }
    }
}