package com.lollipop.debug.floating

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.ImageView
import androidx.compose.runtime.Composable
import com.lollipop.debug.floating.impl.FloatingButtonImpl
import com.lollipop.debug.floating.impl.FloatingPanelImpl
import com.lollipop.debug.floating.utils.FloatingDragHelper

object FloatingHelper {

    val defaultConfig = Config()

    class Config {
        var defaultHeightWeight = 0.5F
        var maxWidthWeight = 0.9F
        var maxHeightWeight = 1F
        var heightOffsetStep = 0.1F
        var minHeightWeight = 0.1F
        var hideOnBackground = true
        var closeOnlyHide = true
    }

    fun createLocalPanel(
        activity: Activity,
        config: Config,
        composeContent: @Composable () -> Unit
    ): Result<FloatingPanel> {
        return try {
            Result.success(createFloatingPanel(activity, false, config, composeContent))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun createOverlayPanel(
        context: Context,
        config: Config,
        composeContent: @Composable () -> Unit
    ): Result<FloatingPanel> {
        val app = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return Result.failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            Result.success(createFloatingPanel(app, true, config, composeContent))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun createLocalButton(
        activity: Activity,
        button: View,
        width: Int,
        height: Int,
        config: Config,
    ): Result<FloatingButton> {
        return try {
            Result.success(
                createFloatingButton(activity, button, width, height, false, config)
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun createOverlayButton(
        context: Context,
        button: View,
        width: Int,
        height: Int,
        config: Config,
    ): Result<FloatingButton> {
        val app = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return Result.failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            Result.success(
                createFloatingButton(app, button, width, height, true, config)
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun createLocalIcon(
        activity: Activity,
        iconId: Int,
        width: Int,
        height: Int,
        config: Config,
        onClickListener: OnClickListener
    ): Result<FloatingButton> {
        return try {
            Result.success(
                createFloatingIcon(activity, iconId, width, height, false, config, onClickListener)
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun createOverlayIcon(
        context: Context,
        iconId: Int,
        width: Int,
        height: Int,
        config: Config,
        onClickListener: OnClickListener
    ): Result<FloatingButton> {
        val app = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(app)) {
                return Result.failure(IllegalStateException("Overlay permission denied"))
            }
        }
        return try {
            Result.success(
                createFloatingIcon(app, iconId, width, height, true, config, onClickListener)
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun createFloatingIcon(
        context: Context,
        iconId: Int,
        width: Int,
        height: Int,
        isOverlay: Boolean,
        config: Config,
        onClickListener: OnClickListener
    ): FloatingButton {
        val button = ImageView(context).apply {
            setImageResource(iconId)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener(onClickListener)
        }
        return createFloatingButton(context, button, width, height, isOverlay, config)
    }

    private fun createFloatingButton(
        context: Context,
        button: View,
        width: Int,
        height: Int,
        isOverlay: Boolean,
        config: Config,
    ): FloatingButton {
        val floatingButton = FloatingButtonImpl(button)
        floatingButton.setHideOnBackground(config.hideOnBackground)
        addViewToWindow(context, floatingButton.view, isOverlay) { m, v, p ->
            p.width = width
            p.height = height
            p.x = 0
            p.y = 0
            p.gravity = 0
            p.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            v.setOnTouchListener(FloatingDragHelper { dx, dy ->
                FloatingDragHelper.offsetView(v, m, dx, dy)
            })
        }
        return floatingButton
    }

    private fun createFloatingPanel(
        context: Context,
        isOverlay: Boolean,
        config: Config,
        composeContent: @Composable () -> Unit
    ): FloatingPanel {
        val panelImpl = FloatingPanelImpl(context, composeContent)
        val viewHolder = panelImpl.viewHolder
        val hideOnBackground = config.hideOnBackground
        val closeOnlyHide = config.closeOnlyHide
        addViewToWindow(context, panelImpl.view, isOverlay) { m, v, p ->
            var heightWeight = config.defaultHeightWeight
            val maxWidthWight = config.maxWidthWeight
            val maxHeightWeight = config.maxHeightWeight
            val minHeightWeight = config.minHeightWeight
            val heightOffsetStep = config.heightOffsetStep
            val screenSize = getScreenSize(m)
            p.width = (screenSize.width * maxWidthWight).toInt()
            p.height = (screenSize.height * heightWeight).toInt()
            p.x = 0
            p.y = 0
            p.gravity = 0
            p.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            viewHolder.setOnUpClickListener {
                heightWeight -= heightOffsetStep
                if (heightWeight < minHeightWeight) {
                    heightWeight = minHeightWeight
                }
                val oldHeight = p.height
                val size = getScreenSize(m)
                p.height = (size.height * heightWeight).toInt()
                p.y -= (oldHeight - p.height) / 2
                m.updateViewLayout(v, p)
            }

            viewHolder.setOnDownClickListener {
                heightWeight += heightOffsetStep
                if (heightWeight > maxHeightWeight) {
                    heightWeight = maxHeightWeight
                }
                val oldHeight = p.height
                val size = getScreenSize(m)
                p.height = (size.height * heightWeight).toInt()
                p.y -= (oldHeight - p.height) / 2
                m.updateViewLayout(v, p)
            }

            viewHolder.setHolderTouchListener(
                FloatingDragHelper { dx, dy ->
                    FloatingDragHelper.offsetView(v, m, dx, dy)
                }
            )
        }
        panelImpl.setHideOnBackground(hideOnBackground)
        panelImpl.setCloseOnlyHide(closeOnlyHide)
        return panelImpl
    }

    private fun addViewToWindow(
        context: Context,
        view: View,
        isOverlay: Boolean,
        builder: (WindowManager, View, WindowManager.LayoutParams) -> Unit
    ) {
        val layoutParams = WindowManager.LayoutParams()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (windowManager != null) {
            if (isOverlay) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                }
            } else if (context is Activity) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
                // 如果不是悬浮窗模式，那么context就必须要是Activity
                layoutParams.token = context.window.decorView.windowToken
            } else {
                return
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.format = PixelFormat.TRANSPARENT;
            builder(windowManager, view, layoutParams)
            windowManager.addView(view, layoutParams);
        }
    }

    private fun getScreenSize(windowManager: WindowManager): Size {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            return Size(windowMetrics.bounds.width(), windowMetrics.bounds.height())
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

}