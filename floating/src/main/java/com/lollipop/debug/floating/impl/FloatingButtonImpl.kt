package com.lollipop.debug.floating.impl

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.lollipop.debug.floating.FloatingButton

class FloatingButtonImpl(
    val view: View
) : FloatingButton {

    private val hideOnBackgroundObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            hide()
        }
    }

    var buttonCloseCallback: ((FloatingButtonImpl) -> Unit)? = null

    override fun show() {
        view.isVisible = true
    }

    override fun hide() {
        view.isInvisible = true
    }

    override fun closeFloating() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(hideOnBackgroundObserver)
        buttonCloseCallback?.invoke(this)
    }

    fun setHideOnBackground(enable: Boolean) {
        if (enable) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(hideOnBackgroundObserver)
        } else {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(hideOnBackgroundObserver)
        }
    }

}