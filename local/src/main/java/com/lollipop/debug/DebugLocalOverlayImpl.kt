package com.lollipop.debug

import android.app.Activity
import android.app.Application
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.lollipop.debug.DebugLocal.KEY_REQUEST_OVERLAY_PERMISSION
import com.lollipop.debug.DebugLocal.getMetaBoolean
import com.lollipop.debug.DebugLocal.hasOverlayPermission
import com.lollipop.debug.DebugLocal.requestOverlayPermission
import com.lollipop.debug.basic.DebugLocalBasicImpl
import com.lollipop.debug.floating.FloatingHelper
import com.lollipop.debug.floating.creator.FloatingOverlayCreator
import com.lollipop.debug.panel.DebugPanelContentView
import com.lollipop.debug.toast.DebugToastView

object DebugLocalOverlayImpl : DebugLocalBasicImpl() {

    private var isRequestOverlayPermission = false

    private var creatorImpl: FloatingOverlayCreator? = null

    override fun onInit(app: Application) {
        creatorImpl = FloatingHelper.overlay(app)
        isRequestOverlayPermission = getMetaBoolean(
            app, KEY_REQUEST_OVERLAY_PERMISSION, false
        )
    }

    private fun creator(): FloatingOverlayCreator {
        val impl = creatorImpl
        if (impl != null) {
            return impl
        }
        val newImpl = FloatingHelper.overlay(application!!)
        creatorImpl = newImpl
        return newImpl
    }

    override fun onActivityResumed(activity: Activity) {
        checkPermission(activity)
    }

    private fun checkPermission(activity: Activity) {
        if (InitState.isAllInit) {
            return
        }
        if (hasOverlayPermission(activity)) {
            initDebugLocal(activity, creator())
            return
        }
        if (isRequestOverlayPermission) {
            requestOverlayPermission(activity)
            // 请求过一次，不管给不给，都不要再请求了
            isRequestOverlayPermission = false
        }
    }

    private fun initDebugLocal(activity: Activity, c: FloatingOverlayCreator) {
        initPanel(activity, c)
        initToast(activity, c)
    }

    private fun initPanel(activity: Activity, c: FloatingOverlayCreator) {
        if (InitState.isPanelInit) {
            return
        }
        val context = activity.application
        val contentView = DebugPanelContentView(context)
        val overlayPanel = c.createPanel(content = contentView)
        contentView.resume()
        val floatingPanel = overlayPanel.getOrNull()
        var isShown = false
        c.createIcon(iconId = DebugLocal.floatingButtonIcon) {
            isShown = !isShown
            if (isShown) {
                floatingPanel?.show()
            } else {
                floatingPanel?.hide()
            }
        }
        InitState.isPanelInit = true
    }

    private fun initToast(activity: Activity, c: FloatingOverlayCreator) {
        if (InitState.isToastInit) {
            return
        }
        DebugToastHelper.toastView?.let {
            if (it is View) {
                c.removeViewFromWindow(it)
            }
        }
        val context = activity.application
        val debugToastView = DebugToastView(context)
        DebugToastHelper.toastView = debugToastView
        c.addView(debugToastView) { m, v, p ->
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
        InitState.isToastInit = true
    }

    private object InitState {
        var isPanelInit = false
        var isToastInit = false
        val isAllInit: Boolean
            get() {
                return isPanelInit && isToastInit
            }
    }

}