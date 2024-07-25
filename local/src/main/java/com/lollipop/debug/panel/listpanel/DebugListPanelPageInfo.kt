package com.lollipop.debug.panel.listpanel

import android.annotation.SuppressLint
import com.lollipop.debug.panel.DebugListPanelAdapter
import com.lollipop.debug.panel.DebugListPanelItem
import com.lollipop.debug.panel.DebugListPanelPage

class DebugListPanelPageInfo(
    val name: String,
    private val adapterCallback: DebugListPanelAdapter
) : DebugListPanelPage {

    private val dataList = ArrayList<DebugListPanelItem>()

    val adapter = DebugListPanelPageAdapterImpl(dataList, adapterCallback)

    val data: List<DebugListPanelItem>
        get() {
            return dataList
        }

    private var onResetCallback: OnResetCallback? = null
    private var onAppendCallback: OnAppendCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun reset(list: List<DebugListPanelItem>) {
        dataList.clear()
        dataList.addAll(list)
        adapter.notifyDataSetChanged()
        onResetCallback?.onReset(this)
    }

    override fun append(list: List<DebugListPanelItem>) {
        val size = dataList.size
        dataList.addAll(list)
        adapter.notifyItemRangeInserted(size, list.size)
        onAppendCallback?.onAppend(this)
    }

    override fun remove(index: Int) {
        if (index < 0 || index >= dataList.size) {
            return
        }
        dataList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    override fun insert(index: Int, item: DebugListPanelItem) {
        if (index < 0 || index >= dataList.size) {
            return
        }
        dataList.add(index, item)
        adapter.notifyItemInserted(index)
    }

    fun registerOnReset(callback: OnResetCallback) {
        this.onResetCallback = callback
    }

    fun registerOnAppend(callback: OnAppendCallback) {
        this.onAppendCallback = callback
    }

    fun unregisterOnReset(callback: OnResetCallback) {
        if (this.onResetCallback === callback) {
            this.onResetCallback = null
        }
    }

    fun unregisterOnAppend(callback: OnAppendCallback) {
        if (this.onAppendCallback === callback) {
            this.onAppendCallback = null
        }
    }

    fun refresh() {
        adapterCallback.onRefresh()
    }

    fun loadMore() {
        adapterCallback.onLoadMore()
    }

    fun canLoadMore(): Boolean {
        return adapterCallback.canLoadMore()
    }

    fun canRefresh(): Boolean {
        return adapterCallback.canRefresh()
    }

    fun interface OnResetCallback {
        fun onReset(page: DebugListPanelPageInfo)
    }

    fun interface OnAppendCallback {
        fun onAppend(page: DebugListPanelPageInfo)
    }

}