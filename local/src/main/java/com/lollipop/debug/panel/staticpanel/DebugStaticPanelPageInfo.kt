package com.lollipop.debug.panel.staticpanel

import com.lollipop.debug.panel.DebugStaticPanelPage

class DebugStaticPanelPageInfo(
    id: String,
    name: String,
) : BasicDebugStaticGroupInfo(id, name), DebugStaticPanelPage {

    var changeMode = 0
        private set

    override fun onItemChanged() {
        changeMode++
    }

    override fun createGroup(id: String, name: String): BasicDebugStaticGroupInfo {
        return DebugStaticPanelGroupInfo(id, name) { onItemChanged() }
    }

}