package com.lollipop.debug.floating.impl

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.databinding.LDebugFloatingPanelBinding
import com.lollipop.debug.floating.utils.FloatingDragHelper
import com.lollipop.debug.floating.utils.FloatingSavedStateRegistryOwner

abstract class BasicFloatingPanel(
    protected val context: Context,
) : FloatingPanel, LifecycleOwner, ViewModelStoreOwner {

    protected val savedStateRegistryController = FloatingSavedStateRegistryOwner()

    protected val floatingViewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle
        get() {
            return savedStateRegistryController.lifecycle
        }

    override val viewModelStore: ViewModelStore
        get() {
            return floatingViewModelStore
        }

    val viewHolder = FloatingPanelViewHolder.create(context)

    var isClosed = false
        private set

    protected var closeOnlyHide = true
        private set

    val view: VisibleStateGroup
        get() {
            return viewHolder.root
        }

    protected val hideOnBackgroundObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            hide()
        }
    }

    init {
        initPanel()
    }

    private fun initPanel() {
        savedStateRegistryController.onCreate()
        setViewTreeLifecycle(view)
        viewHolder.setContent(createPanelContent(context))
        viewHolder.setOnCloseClickListener {
            onCloseClick()
        }
    }

    protected open fun onCloseClick() {
        if (closeOnlyHide) {
            hide()
        } else {
            close()
        }
    }

    private fun setViewTreeLifecycle(view: View) {
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(savedStateRegistryController)
    }

    override fun show() {
        if (isClosed) {
            return
        }
        savedStateRegistryController.onStart()
        savedStateRegistryController.onResume()
        view.isVisible = true
    }

    override fun hide() {
        if (isClosed) {
            return
        }
        view.isInvisible = true
        savedStateRegistryController.onPause()
        savedStateRegistryController.onStop()
    }

    override fun close() {
        if (isClosed) {
            return
        }
        if (view.isVisible) {
            hide()
        }
        savedStateRegistryController.onDestroy()
        viewHolder.destroy()
        isClosed = true
        ProcessLifecycleOwner.get().lifecycle.removeObserver(hideOnBackgroundObserver)
    }

    abstract fun createPanelContent(
        context: Context,
    ): View

    fun setHideOnBackground(enable: Boolean) {
        if (enable) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(hideOnBackgroundObserver)
        } else {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(hideOnBackgroundObserver)
        }
    }

    fun setCloseOnlyHide(enable: Boolean) {
        closeOnlyHide = enable
    }

    class FloatingPanelViewHolder(
        private val binding: LDebugFloatingPanelBinding
    ) {

        companion object {
            fun create(context: Context): FloatingPanelViewHolder {
                return FloatingPanelViewHolder(
                    LDebugFloatingPanelBinding.inflate(
                        LayoutInflater.from(context)
                    )
                )
            }
        }

        val root: VisibleStateGroup
            get() {
                return binding.root
            }

        @SuppressLint("ClickableViewAccessibility")
        fun setHolderTouchListener(listener: View.OnTouchListener) {
            binding.touchHolder.setOnTouchListener(listener)
        }

        fun setContent(view: View) {
            binding.contentGroup.removeAllViews()
            binding.contentGroup.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        fun setOnUpClickListener(callback: () -> Unit) {
            binding.upButton.setOnClickListener {
                callback()
            }
        }

        fun setOnDownClickListener(callback: () -> Unit) {
            binding.downButton.setOnClickListener {
                callback()
            }
        }

        fun setOnCloseClickListener(callback: () -> Unit) {
            binding.closeButton.setOnClickListener {
                callback()
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun destroy() {
            binding.touchHolder.setOnTouchListener(null)
            binding.upButton.setOnClickListener(null)
            binding.downButton.setOnClickListener(null)
            binding.closeButton.setOnClickListener(null)
            binding.contentGroup.removeAllViews()
        }
    }

}