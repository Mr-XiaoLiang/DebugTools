package com.lollipop.debug.panel

import android.view.View
import android.view.ViewGroup

interface DebugListPanelPage {
    fun reset(list: List<DebugListPanelItem>)
    fun add(item: DebugListPanelItem)
    fun remove(index: Int)
    fun insert(index: Int, item: DebugListPanelItem)
}

interface DebugListPanelItem {

    val itemType: Int

}

interface DebugListPanelAdapter {

    fun onCreateView(parent: ViewGroup, itemType: Int): View

    fun onBindView(view: View, item: DebugListPanelItem, position: Int)

}

data object EmptyDebugListPanelPage : DebugListPanelPage {

    override fun reset(list: List<DebugListPanelItem>) {
    }

    override fun add(item: DebugListPanelItem) {
    }

    override fun remove(index: Int) {
    }

    override fun insert(index: Int, item: DebugListPanelItem) {
    }

}

