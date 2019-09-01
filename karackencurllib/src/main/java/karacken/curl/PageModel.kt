package karacken.curl

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class PageModel : ViewModel() {
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

    val res: MutableLiveData<List<String>> = MutableLiveData<List<String>>().default(mutableListOf())

    val currentPosition: MutableLiveData<Int> = MutableLiveData<Int>().default(0)
}