package com.lollipop.debug.core.base

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.lollipop.debug.core.DebugDataProvider

abstract class BasicDebugDataProvider<T : Any>(
    protected val context: Context,
) : DebugDataProvider<T> {

    private var isDestroyed = false

    private val pendingExecutor by lazy {
        PendingExecutor(getProviderName())
    }

    protected open fun getProviderName(): String {
        return "${this::class.simpleName}@${System.identityHashCode(this)}"
    }

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

    protected fun pendingPost(task: PendingTask) {
        pendingExecutor.post(task)
    }

    protected fun pendingDelay(delay: Long, task: PendingTask) {
        pendingExecutor.delay(delay, task)
    }

    protected fun pendingRemove(task: PendingTask) {
        pendingExecutor.remove(task)
    }

    abstract fun onDestroy()

    protected class PendingExecutor(name: String) {

        private val executorThread by lazy {
            HandlerThread(name).apply {
                start()
            }
        }

        private val executorHandler = Handler(executorThread.looper)

        fun post(task: PendingTask) {
            executorHandler.post(task)
        }

        fun delay(delay: Long, task: PendingTask) {
            executorHandler.postDelayed(task, delay)
        }

        fun remove(task: PendingTask) {
            executorHandler.removeCallbacks(task)
        }

    }

    protected interface PendingTask : Runnable

}