package com.lollipop.debug.basic

import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.helper.ListenerManager
import com.lollipop.debug.helper.RunTask
import com.lollipop.debug.helper.onUI

abstract class DebugBasicDataManager<T> {

    private val dataListImpl = ArrayList<T>()

    val data: List<T> get() = dataListImpl

    private val listenerManager = ListenerManager<HttpDataListener>()
    protected open val pageSize = 50
    protected var pageIndex = 0
    var minTime = 0L
        private set

    fun refresh(mode: TimeMode) {
        pageIndex = 0
        when (mode) {
            TimeMode.ALL -> {
                resetMinTime()
            }

            TimeMode.NOW -> {
                minTimeOfNow()
            }
        }
        RefreshTask<T>(this).doAsync()
    }

    fun loadMore() {
        pageIndex++
        LoadMoreTask<T>(this).doAsync()
    }

    protected abstract fun query(
        currentMinTime: Long,
        currentPageSize: Int,
        currentPageIndex: Int
    ): ListResult<T>

    private fun resetMinTime() {
        minTime = 0L
    }

    private fun minTimeOfNow() {
        minTime = System.currentTimeMillis()
    }

    fun addListener(listener: HttpDataListener) {
        listenerManager.add(listener)
    }

    fun removeListener(listener: HttpDataListener) {
        listenerManager.remove(listener)
    }

    private fun notifyRefresh() {
        listenerManager.invoke {
            it.onRefresh()
        }
    }

    private fun notifyLoadMore(position: Int, count: Int) {
        listenerManager.invoke {
            it.onLoadMore(position, count)
        }
    }

    private fun onRefreshResult(result: List<T>) {
        dataListImpl.clear()
        dataListImpl.addAll(result)
        notifyRefresh()
    }

    private fun onLoadMoreResult(result: List<T>) {
        val size = dataListImpl.size
        val count = result.size
        dataListImpl.addAll(result)
        notifyLoadMore(size, count)
    }

    private fun onError(exception: Throwable) {
        listenerManager.invoke { it.onError(exception) }
    }

    private abstract class OptionTask<T>(
        protected val manager: DebugBasicDataManager<T>
    ) : RunTask() {

        private val currentMinTime: Long = manager.minTime
        private val currentPageSize: Int = manager.pageSize
        private val currentPageIndex: Int = manager.pageIndex

        override fun run() {
            val result = manager.query(currentMinTime, currentPageSize, currentPageIndex)
            onDataResult(result)
        }

        abstract fun onDataResult(result: ListResult<T>)

    }

    private class RefreshTask<T>(
        manager: DebugBasicDataManager<T>
    ) : OptionTask<T>(manager) {

        override fun onDataResult(result: ListResult<T>) {
            when (result) {
                is ListResult.Error -> {
                    onUI {
                        manager.onError(result.exception)
                    }
                }

                is ListResult.Success -> {
                    val list = result.data
                    onUI {
                        manager.onRefreshResult(list)
                    }
                }
            }
        }
    }

    private class LoadMoreTask<T>(
        manager: DebugBasicDataManager<T>
    ) : OptionTask<T>(manager) {

        override fun onDataResult(result: ListResult<T>) {
            when (result) {
                is ListResult.Error -> {
                    onUI {
                        manager.onError(result.exception)
                    }
                }

                is ListResult.Success -> {
                    val list = result.data
                    onUI {
                        manager.onLoadMoreResult(list)
                    }
                }
            }
        }
    }

    interface HttpDataListener {
        fun onRefresh()
        fun onLoadMore(startPosition: Int, count: Int)
        fun onError(exception: Throwable)
    }

    enum class TimeMode {
        ALL, NOW
    }

}