package com.lollipop.debug

import android.app.Application

object DebugToastHelper : DToast.DebugToast {

    val toastHistory = mutableListOf<ToastInfo>()

    var toastView: ToastView? = null

    fun init(application: Application) {
        DToast.implements = this
        // TODO 创建悬浮窗
    }

    override fun show(text: String, detail: String) {
        toastHistory.add(ToastInfo(text, detail))
        toastView?.show(text)
    }

    class ToastInfo(
        val text: String,
        val detail: String
    )

    interface ToastView {
        fun show(text: String)
    }

}