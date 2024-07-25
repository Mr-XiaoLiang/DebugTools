package com.lollipop.debug.panel

import android.view.View
import android.view.ViewGroup

interface DebugListPanelPage {
    fun reset(list: List<DebugListPanelItem>)
    fun append(list: List<DebugListPanelItem>)
    fun remove(index: Int)
    fun insert(index: Int, item: DebugListPanelItem)
}

interface DebugListPanelItem {

    val itemType: Int

}

interface DebugListPanelAdapter {

    fun onCreateView(parent: ViewGroup, itemType: Int): View

    fun onBindView(view: View, item: DebugListPanelItem, position: Int)

    fun onRefresh()
    fun onLoadMore()
    fun canLoadMore(): Boolean
    fun canRefresh(): Boolean

}

