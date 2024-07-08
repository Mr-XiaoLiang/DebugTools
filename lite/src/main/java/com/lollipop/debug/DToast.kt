package com.lollipop.debug

import com.lollipop.debug.lite.LiteProxy

object DToast : LiteProxy<DToast.DebugToast>() {

    @JvmStatic
    @JvmOverloads
    fun show(text: String, detail: String = "") {
        invoke { it.show(text, detail) }
    }

    interface DebugToast {
        fun show(text: String, detail: String)
    }

}