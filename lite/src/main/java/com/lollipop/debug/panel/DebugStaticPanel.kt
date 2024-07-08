package com.lollipop.debug.panel

interface DebugStaticPanelPage : DebugStaticPanelGroup

interface DebugStaticPanelGroup {
    fun group(id: String, name: String): DebugStaticPanelGroup
    fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit)
    fun text(id: String, groupId: String, name: String, value: String)
}
