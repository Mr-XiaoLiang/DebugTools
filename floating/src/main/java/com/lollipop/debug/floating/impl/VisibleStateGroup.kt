package com.lollipop.debug.floating.impl

import android.content.Context
import android.util.AttributeSet

class VisibleStateGroup @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : androidx.cardview.widget.CardView(context, attributeSet) {

    private val listeners = mutableListOf<OnVisibilityChangeListener>()

    fun addOnVisibilityChangeListener(listener: OnVisibilityChangeListener) {
        listeners.add(listener)
    }

    fun removeOnVisibilityChangeListener(listener: OnVisibilityChangeListener) {
        listeners.remove(listener)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        listeners.forEach { it.onVisibilityChange(visibility) }
    }

    interface OnVisibilityChangeListener {
        fun onVisibilityChange(visibility: Int)
    }

}