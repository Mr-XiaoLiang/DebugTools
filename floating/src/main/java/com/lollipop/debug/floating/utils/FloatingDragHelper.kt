package com.lollipop.debug.floating.utils

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.updateLayoutParams
import kotlin.math.abs

class FloatingDragHelper(
    val moveTo: (Int, Int) -> Unit
) : View.OnTouchListener {

    companion object {
        fun offsetView(
            view: View,
            windowManager: WindowManager,
            offsetX: Int,
            offsetY: Int,
            minX: Int,
            minY: Int,
            maxX: Int,
            maxY: Int
        ) {
            val layoutParams = view.layoutParams ?: return
            if (layoutParams is WindowManager.LayoutParams) {
                layoutParams.x += offsetX
                layoutParams.y += offsetY
                if (layoutParams.x < minX) {
                    layoutParams.x = minX
                }
                if (layoutParams.x > maxX) {
                    layoutParams.x = maxX
                }
                if (layoutParams.y < minY) {
                    layoutParams.y = minY
                }
                if (layoutParams.y > maxY) {
                    layoutParams.y = maxY
                }
                windowManager.updateViewLayout(view, layoutParams)
            }
        }

        fun offsetView(
            view: View,
            offsetX: Int,
            offsetY: Int,
            minX: Int,
            minY: Int,
            maxX: Int,
            maxY: Int
        ) {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin += offsetX
                topMargin += offsetY
                val left = view.left + offsetX
                val top = view.top + offsetY
                if (left < minX) {
                    leftMargin += minX - left
                }
                if (left > maxX) {
                    leftMargin += maxX - left
                }
                if (top < minY) {
                    topMargin += minY - top
                }
                if (top > maxY) {
                    topMargin += maxY - top
                }
            }
        }

    }

    private var lastTouchX = 0F
    private var lastTouchY = 0F
    private var touchDownX = 0F
    private var touchDownY = 0F

    private var windowManager: WindowManager? = null

    private var dragEnable = false

    private fun MotionEvent.activeX(): Float {
        return rawX
    }

    private fun MotionEvent.activeY(): Float {
        return rawY
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v ?: return false
        event ?: return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                if (x < 0 || x > v.width || y < 0 || y > v.height) {
                    dragEnable = false
                    return false
                }
                dragEnable = true
                lastTouchX = event.activeX()
                lastTouchY = event.activeY()
                touchDownX = lastTouchX
                touchDownY = lastTouchY
                Log.d(
                    "FloatingDragListener",
                    "DOWN: [$touchDownX, $touchDownY] ==> [${event.x}, ${event.y}]"
                )
            }

            MotionEvent.ACTION_MOVE -> {
                if (!dragEnable) {
                    return false
                }
                val x = event.activeX()
                val y = event.activeY()
                val offerX = x - lastTouchX
                val offerY = y - lastTouchY
                lastTouchX = x
                lastTouchY = y
                val oxi = offerX.toInt()
                val oyi = offerY.toInt()
                // 小数点偏差补齐
                lastTouchX -= offerX - oxi
                lastTouchY -= offerY - oyi
                Log.d(
                    "FloatingDragListener",
                    "MOVE: [$x, $y] ==> [$oxi, $oyi]"
                )
                moveTo(oxi, oyi)
            }

            MotionEvent.ACTION_UP -> {
                if (!dragEnable) {
                    return false
                }
                dragEnable = false
                val x = event.activeX()
                val y = event.activeY()
                val touchSlop = ViewConfiguration.get(v.context).scaledTouchSlop
                val totalOffsetX = touchDownX - x
                val totalOffsetY = touchDownY - y
                if (abs(totalOffsetX) < touchSlop && abs(totalOffsetY) < touchSlop) {
                    // 轻触
                    v.performClick()
                } else {
                    // 拖拽
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                dragEnable = false
            }
        }
        return dragEnable
    }

}