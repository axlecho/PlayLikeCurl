package karacken.curl

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import karacken.curl.utils.PLog

enum class PageStatus { TURNING_LEFT, TURNING_RIGHT, NORMAL }
data class PagePosition(val x: Float = 0.0f, val y: Float = 0.0f)
data class PageResource(val data: String)

class PLiveData<T> : MutableLiveData<T>() {
    private var provious: T? = null
    override fun postValue(value: T) {
        provious = this.value
        super.postValue(value)
    }

    override fun setValue(value: T) {
        provious = this.value
        super.setValue(value)
    }

    fun isChanged(): Boolean {
        return provious != this.value
    }
}

class PageModel : ViewModel() {
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

    val imageResources: MutableLiveData<List<PageResource>> = MutableLiveData<List<PageResource>>().default(mutableListOf())
    val imageIndex = PLiveData<Int>()


    fun setImageResources(resources: List<PageResource>) {
        imageResources.default(resources)
        imageIndex.default(0)
    }

    fun imageGoTo(index: Int) {

    }

    fun imageCurrent(): Int? {
        return imageIndex.value
    }

    fun imageGoNext() {
        val index = imageIndex.value ?: return
        PLog.v("image go next - index $index")
        if (imageCanGoNext()) {
            imageIndex.postValue(index + 1)
        }
    }

    fun imageGoPrevious() {
        val index = imageIndex.value ?: return
        if (imageCanGoPrevious()) {
            imageIndex.postValue(index - 1)
        }
    }

    private fun imageCanGoNext(): Boolean {
        val index = imageIndex.value ?: return false
        PLog.v("image go next - index $index")
        val imageResources = imageResources.value ?: return false
        return index < imageResources.size - 1
    }

    private fun imageCanGoPrevious(): Boolean {
        val index = imageIndex.value ?: return false
        return index > 0
    }


    val position: MutableLiveData<PagePosition> = MutableLiveData()
    val status: MutableLiveData<PageStatus> = MutableLiveData<PageStatus>().default(PageStatus.NORMAL)


}