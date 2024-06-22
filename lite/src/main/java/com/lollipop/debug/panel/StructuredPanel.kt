package com.lollipop.debug.panel

interface DebugStructuredPanelPage : DebugStructuredPanelGroup

interface DebugStructuredPanelGroup {
    fun group(id: String, name: String): DebugStructuredPanelGroup
    fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit)
    fun text(id: String, groupId: String, name: String, value: String)
}

object EmptyStructuredPanelView : DebugStructuredPanelPage {
    override fun group(id: String, name: String): DebugStructuredPanelGroup {
        return EmptyStructuredPanelView
    }

    override fun button(
        id: String,
        groupId: String,
        name: String,
        onClickListener: () -> Unit
    ) {
    }

    override fun text(id: String, groupId: String, name: String, value: String) {
    }
}