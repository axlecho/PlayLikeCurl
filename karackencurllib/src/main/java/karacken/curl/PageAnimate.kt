package karacken.curl

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import karacken.curl.utils.PLog
import kotlin.math.abs

class PageAnimate(context: Context) {
    companion object {
        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MAX_OFF_PATH = 250
        const val SWIPE_THRESHOLD_VELOCITY = 200
    }

    val renderer: PageRenderer = PageRenderer(context)
    private var x = 0.0f
    private var pos = 0.0f

    fun animatePagetoDefault(front: PageResource, bg: PageResource) {
        val animateCounter = AnimateCounter.Builder()
                .setCount(100, 0)
                .setDuration(3000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .build()


        animateCounter.setAnimateCounterListener(object : AnimateCounter.AnimateCounterListener {
            override fun onAnimateCounterEnd() {
                PLog.v("resetPages")
                // release module,change the data
                renderer.resetPages(front, bg)
            }

            override fun onValueUpdate(value: Float) {
                val progress = value / 100f
                PLog.v("onValueUpdate $progress")
                // lock module
                renderer.setPercent(progress)
            }
        })
        animateCounter.execute()
    }

    fun onDown(_x: Float) {
        this.x = _x
    }

    fun onMove(_x: Float, width: Int) {
        val distance = (_x - x) / width.toFloat()
        if (_x - x > 0) {  // page_left


            if (pos >= Page.GRID) {
            }
            val value = pos + distance * Page.GRID
            if (value <= Page.GRID) {
            }
        } else if (_x - x < 0) {   //  page_right
            renderer.setPercent(1 - abs(distance))
        }
    }

    fun onUp(_x: Float) {
        renderer.setPercent(1.0f)
    }
}