package com.lollipop.debug

import android.app.Activity
import android.app.Application
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.debug.basic.DebugLocalBasicImpl
import com.lollipop.debug.floating.FloatingHelper
import com.lollipop.debug.floating.FloatingHelper.ButtonConfig
import com.lollipop.debug.floating.FloatingHelper.PanelConfig
import com.lollipop.debug.panel.DebugPanelContentView
import com.lollipop.debug.toast.DebugToastView

object DebugLocalInnerImpl : DebugLocalBasicImpl() {
    override fun onInit(app: Application) {
    }

    override fun onActivityResumed(activity: Activity) {
        initDebugLocal(activity)
    }

    private fun initDebugLocal(activity: Activity) {
        initPanel(activity)
//        initToast(activity)
    }

    private fun initPanel(activity: Activity) {
        val contentView = DebugPanelContentView(activity)
        val overlayPanel =
            FloatingHelper.createLocalPanel(activity, PanelConfig(), contentView)
        contentView.resume()
        if (activity is ComponentActivity) {
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        contentView.destroy()
                    }
                }
            })
        }
        val floatingPanel = overlayPanel.getOrNull()
        var isShown = false
        FloatingHelper.createLocalIcon(activity, DebugLocal.floatingButtonIcon, ButtonConfig()) {
            isShown = !isShown
            if (isShown) {
                floatingPanel?.show()
            } else {
                floatingPanel?.hide()
            }
        }
    }

    private fun initToast(activity: Activity) {
        DebugToastHelper.init()
        val debugToastView = DebugToastView(activity)
        DebugToastHelper.toastView = debugToastView
        FloatingHelper.addViewToWindow(activity, debugToastView, false) { m, v, p ->
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

}