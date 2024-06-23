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

    @SuppressLint("NotifyDataSetChanged")
    override fun reset(list: List<DebugListPanelItem>) {
        dataList.clear()
        dataList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun add(item: DebugListPanelItem) {
        val size = dataList.size
        dataList.add(item)
        adapter.notifyItemInserted(size)
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

}