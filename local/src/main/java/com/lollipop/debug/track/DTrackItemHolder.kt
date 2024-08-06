package com.lollipop.debug.track

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.debug.DToast
import com.lollipop.debug.core.track.DTrackInfo
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryBinding
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryExtraBinding
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryParamsBinding

sealed class DTrackItemHolder(
    binding: ViewBinding,
    val onItemClick: (Int) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onItemClick()
        }
    }

    private fun onItemClick() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            onItemClick(position)
        }
    }

    class Header(
        private val binding: DebugItemTrackHistoryBinding,
        onItemClick: (Int) -> Unit
    ) : DTrackItemHolder(binding, onItemClick) {

        private val actionDrawable = ActionBackgroundDrawable()

        init {
            binding.typeView.background = actionDrawable
            actionDrawable.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f,
                itemView.resources.displayMetrics
            )
            actionDrawable.strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                itemView.resources.displayMetrics
            )
            copyByLongClick(
                binding.timeView,
                binding.typeView,
                binding.targetNameView,
                binding.sourceNameView,
                binding.messageView,
                binding.pageNameView
            )
        }

        fun bind(data: DTrackInfo) {
            binding.timeView.text = data.timeValue
            actionDrawable.color = getActionColor(data.action, itemView.context)
            binding.typeView.text = data.action.uppercase
            binding.targetNameView.text = data.targetName
            binding.sourceNameView.text = data.sourcePage
            binding.messageView.text = data.message
            binding.pageNameView.text = data.pageName
        }

    }

    class Params(
        val binding: DebugItemTrackHistoryParamsBinding
    ) : DTrackItemHolder(binding) {

        init {
            copyByLongClick(binding.paramsKeyView, binding.paramsValueView)
        }

        fun bind(key: String, value: String) {
            binding.paramsKeyView.text = key
            binding.paramsValueView.text = value
        }

    }

    class Extra(
        private val binding: DebugItemTrackHistoryExtraBinding
    ) : DTrackItemHolder(binding) {

        init {
            copyByLongClick(binding.extraValueView)
        }

        fun bind(value: String) {
            binding.extraValueView.text = value
        }

    }

    protected fun getActionColor(action: TrackAction, context: Context): Int {
        val resId = when (action) {
            TrackAction.Background -> R.color.debugTrackItemActionBackground
            TrackAction.Click -> R.color.debugTrackItemActionClick
            TrackAction.Foreground -> R.color.debugTrackItemActionForeground
            TrackAction.Launch -> R.color.debugTrackItemActionLaunch
            is TrackAction.Other -> R.color.debugTrackItemActionOther
            TrackAction.PageHide -> R.color.debugTrackItemActionPageHide
            TrackAction.PageShow -> R.color.debugTrackItemActionPageShow
            TrackAction.Refresh -> R.color.debugTrackItemActionRefresh
        }
        return ContextCompat.getColor(context, resId)
    }

    private class ActionBackgroundDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true

        }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        var strokeWidth: Float
            get() {
                return paint.strokeWidth
            }
            set(value) {
                paint.strokeWidth = value
            }

        var radius: Float = 0f

        private val boundsF = RectF()

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            boundsF.set(bounds)
            val halfStrokeWidth = strokeWidth * 0.5F
            boundsF.inset(halfStrokeWidth, halfStrokeWidth)
            invalidateSelf()
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

    protected fun copyByLongClick(vararg viewArray: TextView) {
        viewArray.forEach {
            it.setOnLongClickListener(TextViewLongClickToCopyListener())
        }
    }

    private class TextViewLongClickToCopyListener : View.OnLongClickListener {

        override fun onLongClick(v: View?): Boolean {
            if (v is TextView) {
                copyText(v)
                return true
            }
            return false
        }

        private fun copyText(view: TextView) {
            val value = view.text?.toString() ?: return
            try {
                val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) ?: return
                if (clipboard is ClipboardManager) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(value, value))
                }
                DToast.show(view.context.getString(R.string.toast_copied))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}

