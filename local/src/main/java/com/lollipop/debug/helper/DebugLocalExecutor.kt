package com.lollipop.debug.helper

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

object DebugLocalExecutor {

    private val executorThread by lazy {
        HandlerThread("DebugLocalExecutor").apply {
            start()
        }
    }

    private val executor by lazy {
        Handler(executorThread.looper)
    }

    private val uiThread = Handler(Looper.getMainLooper())

    fun post(runnable: RunnableTask) {
        executor.post(runnable)
    }

    fun delayed(delay: Long, runnable: RunnableTask) {
        executor.postDelayed(runnable, delay)
    }

    fun remove(runnable: RunnableTask) {
        executor.removeCallbacks(runnable)
    }

    fun onUI(runnable: RunnableTask) {
        uiThread.post(runnable)
    }

    fun removeOnUI(runnable: RunnableTask) {
        uiThread.removeCallbacks(runnable)
    }

    fun delayOnUI(delay: Long, runnable: RunnableTask) {
        uiThread.postDelayed(runnable, delay)
    }

}

fun interface RunnableTask : Runnable

abstract class RunTask : RunnableTask {

    fun doAsync() {
        DebugLocalExecutor.post(this)
    }

    fun onUI() {
        DebugLocalExecutor.onUI(this)
    }

    fun delay(delay: Long) {
        DebugLocalExecutor.delayed(delay, this)
    }

    fun cancel() {
        DebugLocalExecutor.remove(this)
    }

    fun cancelOnUI() {
        DebugLocalExecutor.removeOnUI(this)
    }

    fun delayOnUI(delay: Long) {
        DebugLocalExecutor.delayOnUI(delay, this)
    }

}

class SimpleRunTask(
    private val runnable: () -> Unit
) : RunTask() {
    override fun run() {
        runnable()
    }
}

inline fun <reified T> T.onAsync(crossinline runnable: T.() -> Unit) {
    val context = this
    DebugLocalExecutor.post {
        runnable(context)
    }
}

inline fun <reified T> T.onUI(crossinline runnable: T.() -> Unit) {
    val context = this
    DebugLocalExecutor.onUI {
        runnable(context)
    }
}

inline fun <reified T> T.rememberTask(crossinline runnable: T.() -> Unit): RunTask {
    return SimpleRunTask {
        runnable(this)
    }
}