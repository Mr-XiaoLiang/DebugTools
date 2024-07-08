package com.lollipop.debug.panel

internal class DebugStaticPanelPageWrapper(
    private val implements: List<DebugStaticPanelPage>
) : DebugStaticPanelPage {
    override fun group(id: String, name: String): DebugStaticPanelGroup {
        return DebugStaticPanelGroupWrapper(implements.map { it.group(id, name) })
    }

    override fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit) {
        implements.forEach { it.button(id, groupId, name, onClickListener) }
    }

    override fun text(id: String, groupId: String, name: String, value: String) {
        implements.forEach { it.text(id, groupId, name, value) }
    }

}

internal class DebugStaticPanelGroupWrapper(
    private val implements: List<DebugStaticPanelGroup>
) : DebugStaticPanelGroup {
    override fun group(id: String, name: String): DebugStaticPanelGroup {
        return DebugStaticPanelGroupWrapper(implements.map { it.group(id, name) })
    }

    override fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit) {
        implements.forEach { it.button(id, groupId, name, onClickListener) }
    }

    override fun text(id: String, groupId: String, name: String, value: String) {
        implements.forEach { it.text(id, groupId, name, value) }
    }
}