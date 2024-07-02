package com.lollipop.debug.floating.creator

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.lollipop.debug.floating.FloatingButton
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.FloatingResult
import com.lollipop.debug.floating.impl.BasicFloatingPanel
import com.lollipop.debug.floating.impl.FloatingButtonImpl
import com.lollipop.debug.floating.impl.FloatingPanelComposeImpl
import com.lollipop.debug.floating.impl.FloatingPanelViewImpl
import com.lollipop.debug.floating.utils.FloatingDragHelper
import java.util.LinkedList

class FloatingInnerCreator(val activity: Activity) : FloatingCreator() {

    companion object {
        inline fun <reified T : View> findView(activity: Activity): T? {
            val pending = LinkedList<View>()
            pending.addLast(activity.findViewById(android.R.id.content))
            while (pending.isNotEmpty()) {
                val view = pending.removeFirst()
                if (view is T) {
                    return view
                }
                if (view is ViewGroup) {
                    for (i in 0 until view.childCount) {
                        pending.addLast(view.getChildAt(i))
                    }
                }
            }
            return null
        }
    }

    override fun createPanel(
        config: FloatingPanelConfig,
        composeContent: @Composable () -> Unit
    ): FloatingResult<FloatingPanel> {
        return try {
            FloatingResult.Inner(createFloatingPanel(activity, config, composeContent))
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createPanel(
        config: FloatingPanelConfig,
        content: View
    ): FloatingResult<FloatingPanel> {
        return try {
            FloatingResult.Inner(createFloatingPanel(config, content))
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createButton(
        config: FloatingButtonConfig,
        button: View
    ): FloatingResult<FloatingButton> {
        return try {
            FloatingResult.Inner(
                createFloatingButton(activity, button, config)
            )
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createIcon(
        config: FloatingButtonConfig,
        iconId: Int,
        onClickListener: View.OnClickListener
    ): FloatingResult<FloatingButton> {
        return try {
            FloatingResult.Overlay(
                createFloatingIcon(activity, iconId, config, onClickListener)
            )
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    fun removeViewFromParent(view: View) {
        view.parent?.let {
            if (it is ViewManager) {
                it.removeView(view)
            }
        }
    }

    private fun createFloatingPanel(
        context: Context,
        config: FloatingPanelConfig,
        composeContent: @Composable () -> Unit
    ): FloatingPanel {
        return createFloatingPanel(
            config,
            FloatingPanelComposeImpl(context, composeContent)
        )
    }

    private fun createFloatingPanel(
        config: FloatingPanelConfig,
        viewContent: View
    ): FloatingPanel {
        return createFloatingPanel(
            config,
            FloatingPanelViewImpl(viewContent.context, viewContent)
        )
    }

    private fun createFloatingPanel(
        config: FloatingPanelConfig,
        panelImpl: BasicFloatingPanel
    ): FloatingPanel {
        val viewHolder = panelImpl.viewHolder
        val hideOnBackground = config.hideOnBackground
        val closeOnlyHide = config.closeOnlyHide

        val view = panelImpl.view
        panelImpl.panelCloseCallback = {
            removeViewFromParent(view)
        }
        var heightWeight = config.defaultHeightWeight
        val maxWidthWight = config.maxWidthWeight
        val maxHeightWeight = config.maxHeightWeight
        val minHeightWeight = config.minHeightWeight
        val heightOffsetStep = config.heightOffsetStep
        addView(view) { r, v, p ->
            val groupWidth = r.width
            val groupHeight = r.height
            p.width = (groupWidth * maxWidthWight).toInt()
            p.height = (groupHeight * heightWeight).toInt()
            p.gravity = 0
        }
        viewHolder.setOnUpClickListener {
            val groupHeight = view.parentHeight((view.height / heightWeight).toInt())
            heightWeight -= heightOffsetStep
            if (heightWeight < minHeightWeight) {
                heightWeight = minHeightWeight
            }
            view.updateLayoutParams<FrameLayout.LayoutParams> {
                height = (groupHeight * heightWeight).toInt()
            }
        }

        viewHolder.setOnDownClickListener {
            val groupHeight = view.parentHeight((view.height / heightWeight).toInt())
            heightWeight += heightOffsetStep
            if (heightWeight > maxHeightWeight) {
                heightWeight = maxHeightWeight
            }
            view.updateLayoutParams<FrameLayout.LayoutParams> {
                height = (groupHeight * heightWeight).toInt()
            }
        }

        val offsetHelper = FloatingDragHelper.offsetView(view)
        view.post { offsetHelper.bindParentBounds() }
        offsetHelper.bindWindowInsets(WindowInsetsCompat.Type.systemBars())
        viewHolder.setHolderTouchListener(FloatingDragHelper(offsetHelper))
        panelImpl.setHideOnBackground(hideOnBackground)
        panelImpl.setCloseOnlyHide(closeOnlyHide)
        return panelImpl
    }

    private fun createFloatingIcon(
        context: Context,
        iconId: Int,
        config: FloatingButtonConfig,
        onClickListener: OnClickListener
    ): FloatingButton {
        val button = ImageView(context).apply {
            setImageResource(iconId)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener(onClickListener)
        }
        return createFloatingButton(context, button, config)
    }

    private fun createFloatingButton(
        context: Context,
        button: View,
        config: FloatingButtonConfig,
    ): FloatingButton {
        val floatingButton = FloatingButtonImpl(button)
        floatingButton.buttonCloseCallback = {
            removeViewFromParent(floatingButton.view)
        }
        floatingButton.setHideOnBackground(config.hideOnBackground)
        val iconWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            config.widthDp,
            context.resources.displayMetrics
        ).toInt()
        val iconHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            config.heightDp,
            context.resources.displayMetrics
        ).toInt()
        addView(floatingButton.view) { r, v, p ->
            p.width = iconWidth
            p.height = iconHeight
            p.gravity = 0
        }
        val offsetHelper = FloatingDragHelper.offsetView(floatingButton.view)
        floatingButton.view.post { offsetHelper.bindParentBounds() }
        offsetHelper.bindWindowInsets(WindowInsetsCompat.Type.systemBars())
        floatingButton.view.setOnTouchListener(FloatingDragHelper(offsetHelper))
        return floatingButton
    }

    fun addView(
        view: View,
        builder: (ViewGroup, View, FrameLayout.LayoutParams) -> Unit
    ) {
        getRootGroup().let { root ->
            root.post {
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                builder(root, view, params)
                root.addView(view, params)
            }
        }
    }

    private fun View.parentHeight(def: Int = Int.MAX_VALUE): Int {
        val parent = parent
        return if (parent is View) {
            parent.height
        } else {
            def
        }
    }

    private fun View.parentWidth(def: Int = Int.MAX_VALUE): Int {
        val parent = parent
        return if (parent is View) {
            parent.width
        } else {
            def
        }
    }

    private fun getRootGroup(): ViewGroup {
        val decorView = activity.window.decorView
        if (decorView is ViewGroup) {
            return decorView
        }
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        if (contentView is ViewGroup) {
            return contentView
        }
        throw IllegalStateException("Can not find the root group")
    }

}