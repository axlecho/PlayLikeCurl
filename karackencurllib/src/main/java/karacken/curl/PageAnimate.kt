package karacken.curl

import android.content.Context
import android.view.animation.Interpolator

class PageAnimate(context: Context) {
    companion object {
        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MAX_OFF_PATH = 250
        const val SWIPE_THRESHOLD_VELOCITY = 200
    }

    val renderer: PageRenderer = PageRenderer(context)
    private var x = 0.0f
    private var pos = 0.0f

    fun animatePagetoDefault(interpolator: Interpolator) {
        val animateCounter = AnimateCounter.Builder()
                .setCount(0, 100)
                .setDuration(300)
                .setInterpolator(interpolator)
                .build()


        animateCounter.setAnimateCounterListener(object : AnimateCounter.AnimateCounterListener {
            override fun onAnimateCounterEnd() {
                renderer.resetPages()
            }

            override fun onValueUpdate(value: Float) {
                renderer.setPercent(value / 100f)
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
            val value = (1 - Math.abs(distance)) * Page.GRID - (Page.GRID - pos)
        }
    }
}