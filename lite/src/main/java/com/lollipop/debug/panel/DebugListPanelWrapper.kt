package com.lollipop.debug.panel

internal class DebugListPanelPageWrapper(
    private val implements: List<DebugListPanelPage>
) : DebugListPanelPage {
    override fun reset(list: List<DebugListPanelItem>) {
        implements.forEach { it.reset(list) }
    }

    override fun add(item: DebugListPanelItem) {
        implements.forEach { it.add(item) }
    }

    override fun remove(index: Int) {
        implements.forEach { it.remove(index) }
    }

    override fun insert(index: Int, item: DebugListPanelItem) {
        implements.forEach { it.insert(index, item) }
    }
}

