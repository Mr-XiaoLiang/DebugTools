package com.lollipop.debug.panel

interface DebugStaticPanelPage : DebugStaticPanelGroup

interface DebugStaticPanelGroup {
    fun group(id: String, name: String): DebugStaticPanelGroup
    fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit)
    fun text(id: String, groupId: String, name: String, value: String)
}

object EmptyStaticPanelView : DebugStaticPanelPage {
    override fun group(id: String, name: String): DebugStaticPanelGroup {
        return EmptyStaticPanelView
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