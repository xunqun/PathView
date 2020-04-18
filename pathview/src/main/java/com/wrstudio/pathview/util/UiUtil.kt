package com.wrstudio.pathview.util

import android.content.res.Resources

class UiUtil {
    companion object {
        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}