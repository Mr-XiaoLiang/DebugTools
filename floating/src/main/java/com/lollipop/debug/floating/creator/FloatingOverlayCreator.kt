package com.lollipop.debug.floating.creator

import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.ImageView
import androidx.compose.runtime.Composable
import com.lollipop.debug.floating.FloatingButton
import com.lollipop.debug.floating.FloatingHelper
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.FloatingResult
import com.lollipop.debug.floating.impl.BasicFloatingPanel
import com.lollipop.debug.floating.impl.FloatingButtonImpl
import com.lollipop.debug.floating.impl.FloatingPanelComposeImpl
import com.lollipop.debug.floating.impl.FloatingPanelViewImpl
import com.lollipop.debug.floating.utils.FloatingDragHelper
import kotlin.math.min

class FloatingOverlayCreator(private val app: Application) : FloatingCreator() {

    override fun createPanel(
        config: FloatingPanelConfig,
        composeContent: @Composable () -> Unit
    ): FloatingResult<FloatingPanel> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return FloatingResult.Failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            FloatingResult.Overlay(createFloatingPanel(app, config, composeContent))
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createPanel(
        config: FloatingPanelConfig,
        content: View
    ): FloatingResult<FloatingPanel> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return FloatingResult.Failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            FloatingResult.Overlay(createFloatingPanel(config, content))
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createButton(
        config: FloatingButtonConfig,
        button: View,
    ): FloatingResult<FloatingButton> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return FloatingResult.Failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            FloatingResult.Overlay(
                createFloatingButton(app, button, config)
            )
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }

    override fun createIcon(
        config: FloatingButtonConfig,
        iconId: Int,
        onClickListener: OnClickListener
    ): FloatingResult<FloatingButton> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return FloatingResult.Failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            FloatingResult.Overlay(
                createFloatingIcon(app, iconId, config, onClickListener)
            )
        } catch (e: Throwable) {
            FloatingResult.Failure(e)
        }
    }


    fun removeViewFromWindow(view: View) {
        getWindowManager(view.context)?.removeView(view)
    }

    fun addView(
        view: View,
        builder: (WindowManager, View, WindowManager.LayoutParams) -> Unit
    ) {
        val context = view.context
        val layoutParams = WindowManager.LayoutParams()
        val windowManager = getWindowManager(context)
        if (windowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.format = PixelFormat.TRANSPARENT;
            builder(windowManager, view, layoutParams)
            windowManager.addView(view, layoutParams);
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
        floatingButton.buttonCloseCallback = {
            removeViewFromWindow(floatingButton.view)
        }
        floatingButton.setHideOnBackground(config.hideOnBackground)
        addView(floatingButton.view) { m, v, p ->
            p.width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                config.widthDp,
                context.resources.displayMetrics
            ).toInt()
            p.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                config.heightDp,
                context.resources.displayMetrics
            ).toInt()
            p.x = config.defaultX
            p.y = config.defaultY
            p.gravity = 0
            p.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

            val offsetHelper = FloatingDragHelper.offsetView(v, m)
            val screenSize = FloatingHelper.getScreenSize(m)
            val halfWidth = screenSize.width / 2
            val halfHeight = screenSize.height / 2
            offsetHelper.setBounds(
                halfWidth * -1,
                halfHeight * -1,
                halfWidth,
                halfHeight
            )
            v.setOnTouchListener(FloatingDragHelper(offsetHelper))
        }
        return floatingButton
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
            removeViewFromWindow(view)
        }
        addView(view) { m, v, p ->
            var heightWeight = config.defaultHeightWeight
            val maxWidthWight = config.maxWidthWeight
            val maxHeightWeight = config.maxHeightWeight
            val minHeightWeight = config.minHeightWeight
            val heightOffsetStep = config.heightOffsetStep
            val screenSize = FloatingHelper.getScreenSize(m)
            // 为了避免横屏时候过宽，所以这里取短边
            p.width = (min(screenSize.width, screenSize.height) * maxWidthWight).toInt()
            // 高度只要按照当前的高度计算即可
            p.height = (screenSize.height * heightWeight).toInt()
            p.x = 0
            p.y = 0
            p.gravity = 0
            p.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val offsetHelper = FloatingDragHelper.offsetView(v, m)
            val halfWidth = screenSize.width / 2
            val halfHeight = screenSize.height / 2
            val halfPanelHeight = p.height / 2
            offsetHelper.setBounds(
                halfWidth * -1,
                halfHeight * -1 + halfPanelHeight,
                halfWidth,
                halfHeight
            )

            viewHolder.setOnUpClickListener {
                heightWeight -= heightOffsetStep
                if (heightWeight < minHeightWeight) {
                    heightWeight = minHeightWeight
                }
                val oldHeight = p.height
                val size = FloatingHelper.getScreenSize(m)
                p.height = (size.height * heightWeight).toInt()
                p.y -= (oldHeight - p.height) / 2
                m.updateViewLayout(v, p)

                offsetHelper.setBounds(
                    halfWidth * -1,
                    halfHeight * -1 + halfPanelHeight,
                    halfWidth,
                    halfHeight
                )
            }

            viewHolder.setOnDownClickListener {
                heightWeight += heightOffsetStep
                if (heightWeight > maxHeightWeight) {
                    heightWeight = maxHeightWeight
                }
                val oldHeight = p.height
                val size = FloatingHelper.getScreenSize(m)
                p.height = (size.height * heightWeight).toInt()
                p.y -= (oldHeight - p.height) / 2
                m.updateViewLayout(v, p)

                offsetHelper.setBounds(
                    halfWidth * -1,
                    halfHeight * -1 + halfPanelHeight,
                    halfWidth,
                    halfHeight
                )
            }
            viewHolder.setHolderTouchListener(FloatingDragHelper(offsetHelper))
        }
        panelImpl.setHideOnBackground(hideOnBackground)
        panelImpl.setCloseOnlyHide(closeOnlyHide)
        return panelImpl
    }

    private fun getWindowManager(context: Context): WindowManager? {
        return context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    }

}
