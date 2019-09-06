package karacken.curl

import karacken.curl.page.PageFront

class PageReader(model: PageModel, private val view: PageSurfaceView) {

    private val  pageFront = PageFront
    private val  pageCurrent = PageFront
    private val  pageBackground = PageFront

    init {
        model.currentPosition.observeForever { index -> index?.let { goTo(it) } }
    }

    fun goTo(index:Int) {
        // viex.ectuAnimate()
        // pageFront.setResource(bitmap[index - 1])
        // pageCurrent.setResource(bitmap[index])
        // pageBackground.setResource(bitmap[index + 1])
    }


}