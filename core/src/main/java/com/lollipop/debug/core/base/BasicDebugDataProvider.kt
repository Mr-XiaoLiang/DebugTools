package com.lollipop.debug.core.base

import android.content.Context
import com.lollipop.debug.core.DebugDataProvider

abstract class BasicDebugDataProvider<T : Any>(
    protected val context: Context
) : DebugDataProvider<T> {

    private var isDestroyed = false

    override fun select(pageSize: Int, pageIndex: Int): List<T> {
        if (isDestroyed) {
            return emptyList()
        }
        return onSelect(pageSize, pageIndex)
    }

    abstract fun onSelect(pageSize: Int, pageIndex: Int): List<T>

    fun destroy() {
        isDestroyed = true
        onDestroy()
    }

    abstract fun onDestroy()

}