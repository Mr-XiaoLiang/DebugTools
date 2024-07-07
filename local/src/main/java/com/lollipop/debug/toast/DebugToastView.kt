package com.lollipop.debug.toast

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.DebugToastHelper
import java.util.LinkedList

class DebugToastView(context: Context) : FrameLayout(context), DebugToastHelper.ToastView {

    companion object {
        var toastBackgroundRadiusDp = 12F
    }

    private val delayHandler = Handler(Looper.getMainLooper())

    private val toastList = LinkedList<ToastInfo>()

    private val adapter = ToastItemAdapter(toastList)

    private val autoRemoveTask = Runnable {
        if (toastList.isNotEmpty()) {
            toastList.removeFirst()
            adapter.notifyItemRemoved(0)
        }
        if (toastList.isEmpty()) {
            postHidePanel()
        }
    }

    private val autoHideTask = Runnable {
        if (toastList.isEmpty()) {
            hidePanel()
        }
    }

    private val recyclerView = RecyclerView(context)

    init {
        addView(
            recyclerView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        postHidePanel()
    }

    override fun show(text: String) {
        if (!isVisible) {
            isVisible = true
        }
        if (toastList.isEmpty()) {
            delayHandler.removeCallbacks(autoHideTask)
        }
        val size = toastList.size
        toastList.addLast(ToastInfo(text))
        adapter.notifyItemInserted(size)
        recyclerView.scrollToPosition(size)
        delayHandler.postDelayed(autoRemoveTask, 3000)
    }

    private fun postHidePanel() {
        delayHandler.removeCallbacks(autoHideTask)
        delayHandler.postDelayed(autoHideTask, 1000)
    }

    private fun hidePanel() {
        this.isVisible = false
    }

    private class ToastItemAdapter(
        val data: List<ToastInfo>
    ) : RecyclerView.Adapter<ToastItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToastItemHolder {
            return ToastItemHolder.create(parent.context)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ToastItemHolder, position: Int) {
            holder.bind(data[position])
        }
    }

    private class ToastItemHolder(
        private val textView: TextView
    ) : RecyclerView.ViewHolder(textView) {

        companion object {
            fun create(context: Context): ToastItemHolder {
                val textView = TextView(context).apply {
                    setTextColor(0xFFFFFFFF.toInt())
                    gravity = Gravity.CENTER
                    textSize = 14F
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8F,
                        context.resources.displayMetrics
                    ).toInt()
                    setPadding(dp8 * 2, dp8, dp8 * 2, dp8)
                    val lp =
                        MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    lp.leftMargin = dp8 / 2
                    lp.rightMargin = dp8 / 2
                    lp.topMargin = dp8 / 4
                    lp.bottomMargin = dp8 / 4
                    layoutParams = lp
                    background = ToastItemBackground(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            toastBackgroundRadiusDp,
                            context.resources.displayMetrics
                        ),
                        0x88000000.toInt()
                    )
                }
                return ToastItemHolder(textView)
            }
        }

        fun bind(info: ToastInfo) {
            textView.text = info.value
        }

    }

    private class ToastItemBackground(
        val radius: Float,
        val backgroundColor: Int
    ) : Drawable() {

        private val boundsF = RectF()

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            color = backgroundColor
        }

        override fun onBoundsChange(bounds: android.graphics.Rect) {
            super.onBoundsChange(bounds)
            boundsF.set(bounds)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(boundsF, radius, radius, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

    private class ToastInfo(
        val value: String
    )

}