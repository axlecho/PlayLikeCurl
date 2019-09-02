package karacken.curl.utils

import android.util.Log

class PLog {
    companion object {
        private const val TAG = "PLog"
        fun v(msg:String) {
            Log.v(TAG,msg)
        }
    }
}