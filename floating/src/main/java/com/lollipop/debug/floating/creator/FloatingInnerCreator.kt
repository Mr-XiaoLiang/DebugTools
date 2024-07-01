package com.lollipop.debug.floating.creator

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.runtime.Composable
import com.lollipop.debug.floating.FloatingButton
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.FloatingResult
import com.lollipop.debug.floating.impl.FloatingButtonImpl
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
        TODO("Not yet implemented")
    }

    override fun createPanel(
        config: FloatingPanelConfig,
        content: View
    ): FloatingResult<FloatingPanel> {
        TODO("Not yet implemented")
    }

    override fun createButton(
        config: FloatingButtonConfig,
        button: View
    ): FloatingResult<FloatingButton> {
        TODO("Not yet implemented")
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
        addView(floatingButton.view) { p ->
            p.width = iconWidth
            p.height = iconHeight
            p.gravity = 0
        }
        floatingButton.view.setOnTouchListener(FloatingDragHelper { dx, dy ->
            val parent = floatingButton.view.parent
            FloatingDragHelper.offsetView(
                floatingButton.view,
                dx,
                dy,
                0,
                0,
                if (parent is ViewGroup) {
                    parent.width - iconWidth
                } else {
                    Int.MAX_VALUE
                },
                if (parent is ViewGroup) {
                    parent.height - iconHeight
                } else {
                    Int.MAX_VALUE
                }
            )
        })
        return floatingButton
    }

    fun addView(
        view: View,
        builder: (FrameLayout.LayoutParams) -> Unit
    ) {
        getRootGroup().let { root ->
            root.post {
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                builder(params)
                root.addView(view, params)
            }
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