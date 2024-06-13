package com.lollipop.debug

object DToast {

    var implements: DebugToast? = null

    @JvmStatic
    @JvmOverloads
    fun show(text: String, detail: String = "") {
        implements?.show(text, detail)
    }

    interface DebugToast {
        fun show(text: String, detail: String)
    }

}