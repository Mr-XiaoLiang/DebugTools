package com.lollipop.debug.floating.utils

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import kotlin.math.abs

class FloatingDragHelper(
    val offsetController: OffsetController
) : View.OnTouchListener {

    companion object {
        fun offsetView(
            view: View,
            windowManager: WindowManager,
        ): WindowOffsetHelper {
            return WindowOffsetHelper(view, windowManager)
        }

        fun offsetView(
            view: View,
        ): ViewOffsetHelper {
            return ViewOffsetHelper(view)
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
                offsetController.offset(oxi, oyi)
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

    fun interface OffsetController {
        fun offset(offsetX: Int, offsetY: Int)
    }

    class ViewOffsetHelper(private val view: View) : OffsetController {
        private var topEdge = 0
        private var leftEdge = 0
        private var rightEdge = 0
        private var bottomEdge = 0

        private var minX: Int = 0
        private var minY: Int = 0
        private var maxX: Int = 0
        private var maxY: Int = 0

        fun setBounds(minX: Int, minY: Int, maxX: Int, maxY: Int) {
            this.minX = minX
            this.minY = minY
            this.maxX = maxX
            this.maxY = maxY
            offset(0, 0)
        }

        fun bindParentBounds() {
            val parent = view.parent
            if (parent is View) {
                parent.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    val viewWidth = view.width
                    val viewHeight = view.height
                    setBounds(
                        0,
                        0,
                        if (parent is ViewGroup) {
                            parent.width - viewWidth
                        } else {
                            Int.MAX_VALUE
                        },
                        if (parent is ViewGroup) {
                            parent.height - viewHeight
                        } else {
                            Int.MAX_VALUE
                        }
                    )
                    offset(0, 0)
                }
            }
        }

        fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
            this.topEdge = top
            this.leftEdge = left
            this.rightEdge = right
            this.bottomEdge = bottom
            offset(0, 0)
        }

        fun bindWindowInsets(insertsType: Int) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, inserts ->
                val systemBars = inserts.getInsets(insertsType)
                setInsets(
                    systemBars.top,
                    systemBars.left,
                    systemBars.right,
                    systemBars.bottom,
                )
                offset(0, 0)
                inserts
            }
        }

        override fun offset(offsetX: Int, offsetY: Int) {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin += offsetX
                topMargin += offsetY
                val left = view.left + offsetX
                val top = view.top + offsetY
                val minXOffset = minX + leftEdge
                val minYOffset = minY + topEdge
                val maxXOffset = maxX - rightEdge
                val maxYOffset = maxY - bottomEdge

                if (left < minXOffset) {
                    leftMargin += minXOffset - left
                }
                if (left > maxXOffset) {
                    leftMargin += maxXOffset - left
                }
                if (top < minYOffset) {
                    topMargin += minYOffset - top
                }
                if (top > maxYOffset) {
                    topMargin += maxYOffset - top
                }
            }
        }

    }

    class WindowOffsetHelper(
        private val view: View,
        private val windowManager: WindowManager
    ) : OffsetController {
        private var topEdge = 0
        private var leftEdge = 0
        private var rightEdge = 0
        private var bottomEdge = 0

        private var minX: Int = 0
        private var minY: Int = 0
        private var maxX: Int = 0
        private var maxY: Int = 0

        fun setBounds(minX: Int, minY: Int, maxX: Int, maxY: Int) {
            this.minX = minX
            this.minY = minY
            this.maxX = maxX
            this.maxY = maxY
            offset(0, 0)
        }

        fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
            this.topEdge = top
            this.leftEdge = left
            this.rightEdge = right
            this.bottomEdge = bottom
            offset(0, 0)
        }

        override fun offset(offsetX: Int, offsetY: Int) {
            val layoutParams = view.layoutParams ?: return
            if (layoutParams is WindowManager.LayoutParams) {
                layoutParams.x += offsetX
                layoutParams.y += offsetY

                val minXOffset = minX + leftEdge
                val minYOffset = minY + topEdge
                val maxXOffset = maxX - rightEdge
                val maxYOffset = maxY - bottomEdge

                if (layoutParams.x < minXOffset) {
                    layoutParams.x = minXOffset
                }
                if (layoutParams.x > maxXOffset) {
                    layoutParams.x = maxXOffset
                }
                if (layoutParams.y < minYOffset) {
                    layoutParams.y = minYOffset
                }
                if (layoutParams.y > maxYOffset) {
                    layoutParams.y = maxYOffset
                }
                windowManager.updateViewLayout(view, layoutParams)
            }
        }
    }

}