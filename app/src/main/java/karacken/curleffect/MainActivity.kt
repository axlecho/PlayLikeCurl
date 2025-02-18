package karacken.curleffect

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import karacken.curl.PageModel
import karacken.curl.PageResource
import karacken.curl.PageSurfaceView

/**
 * Created by karacken on 18/11/16.
 */
class MainActivity : AppCompatActivity() {


    private var screen_width: Int = 0
    private var screen_height: Int = 0

    private lateinit var pageSurfaceView: PageSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = ViewModelProviders.of(this).get(PageModel::class.java)
        pageSurfaceView = PageSurfaceView(this, model)
        setContentView(pageSurfaceView)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            model.setImageResources(arrayListOf(PageResource("portrait/page1.png"),
                    PageResource("portrait/page2.png"),
                    PageResource("portrait/page3.png"),
                    PageResource("portrait/page4.png"),
                    PageResource("portrait/page5.png")))

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screen_width = size.x
        screen_height = size.y

    }


    override fun onResume() {
        super.onResume()
        pageSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        pageSurfaceView.onPause()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return pageSurfaceView.onPageTouchEvent(event)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }


}
