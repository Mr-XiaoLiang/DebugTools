package com.lollipop.debug.panel.staticpanel

class DebugStaticPanelGroupInfo(
    id: String,
    name: String,
    private val onChangedCallback: () -> Unit
) : BasicDebugStaticGroupInfo(id, name) {

    override fun onItemChanged() {
        onChangedCallback()
    }

    override fun createGroup(id: String, name: String): BasicDebugStaticGroupInfo {
        return DebugStaticPanelGroupInfo(id, name, onChangedCallback)
    }

}
