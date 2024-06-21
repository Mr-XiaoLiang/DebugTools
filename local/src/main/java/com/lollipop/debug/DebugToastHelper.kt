package com.lollipop.debug

import android.app.Application
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.lollipop.debug.floating.FloatingHelper
import com.lollipop.debug.toast.DebugToastView

object DebugToastHelper : DToast.DebugToast {

    val toastHistory = mutableListOf<ToastInfo>()

    private var toastView: ToastView? = null

    fun init(application: Application) {
        DToast.implements = this
        toastView?.let {
            if (it is View) {
                FloatingHelper.removeViewFromWindow(it)
            }
        }
        val debugToastView = DebugToastView(application)
        toastView = debugToastView
        FloatingHelper.addViewToWindow(application, debugToastView, true) { m, v, p ->
            val screenSize = FloatingHelper.getScreenSize(m)
            p.width = (screenSize.width * 0.4F).toInt()
            p.height = (screenSize.height * 0.4F).toInt()
            p.flags = FloatingHelper.flagsOf(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            )
            p.gravity = FloatingHelper.flagsOf(
                Gravity.RIGHT,
                Gravity.TOP
            )
        }
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