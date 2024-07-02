package com.lollipop.debug

import android.app.Activity
import android.app.Application
import android.view.Gravity
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.debug.basic.DebugLocalBasicImpl
import com.lollipop.debug.floating.FloatingHelper
import com.lollipop.debug.floating.creator.FloatingInnerCreator
import com.lollipop.debug.floating.impl.VisibleStateGroup
import com.lollipop.debug.panel.DebugPanelContentView
import com.lollipop.debug.toast.DebugToastView

object DebugLocalInnerImpl : DebugLocalBasicImpl() {

    private inline fun <reified T : View> findView(activity: Activity): T? {
        return FloatingInnerCreator.findView(activity)
    }

    override fun onInit(app: Application) {

    }

    override fun onActivityResumed(activity: Activity) {
        initDebugLocal(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        removeToast(activity)
    }

    private fun initDebugLocal(activity: Activity) {
        val creator = FloatingHelper.inner(activity)
        initPanel(activity, creator)
        initToast(activity, creator)
    }

    private fun initPanel(activity: Activity, creator: FloatingInnerCreator) {
        val visibleStateGroup = findView<VisibleStateGroup>(activity)
        if (visibleStateGroup != null) {
            return
        }
        val contentView = DebugPanelContentView(activity)
        val overlayPanel = creator.createPanel(content = contentView)
        contentView.resume()
        if (activity is ComponentActivity) {
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_DESTROY -> {
                            contentView.destroy()
                        }

                        Lifecycle.Event.ON_STOP -> {
                            contentView.pause()
                        }

                        Lifecycle.Event.ON_START -> {
                            contentView.resume()
                        }

                        else -> {}
                    }
                }
            })
        }
        val floatingPanel = overlayPanel.getOrNull() ?: return
        contentView.post {
            floatingPanel.hide()
        }
        var isShown = false
        creator.createIcon(iconId = DebugLocal.floatingButtonIcon) {
            isShown = !isShown
            if (isShown) {
                floatingPanel.show()
            } else {
                floatingPanel.hide()
            }
        }
    }

    private fun initToast(activity: Activity, creator: FloatingInnerCreator) {
        val currentToastView = findView<DebugToastView>(activity)
        if (currentToastView != null) {
            DebugToastHelper.toastView = currentToastView
        } else {
            DebugToastHelper.init()
            val debugToastView = DebugToastView(activity)
            DebugToastHelper.toastView = debugToastView
            creator.addView(debugToastView) { g, v, p ->
                val screenSize = FloatingHelper.getWindowSize(activity.window)
                p.width = (screenSize.width * 0.4F).toInt()
                p.height = (screenSize.height * 0.4F).toInt()
                p.gravity = FloatingHelper.flagsOf(
                    Gravity.RIGHT,
                    Gravity.TOP
                )
            }
        }
    }

    private fun removeToast(activity: Activity) {
        val currentToastView = findView<DebugToastView>(activity)
        if (currentToastView != null && DebugToastHelper.toastView === currentToastView) {
            DebugToastHelper.toastView = null
        }
    }

}