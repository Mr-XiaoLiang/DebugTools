package com.lollipop.debug

import com.lollipop.debug.toast.ToastInfo

object DebugToastHelper : DToast.DebugToast {

    val toastHistory = mutableListOf<ToastInfo>()

    var toastView: ToastView? = null

    override fun show(text: String, detail: String) {
        toastHistory.add(0, ToastInfo(text, detail, System.currentTimeMillis()))
        toastView?.show(text)
    }

    interface ToastView {
        fun show(text: String)
    }

}