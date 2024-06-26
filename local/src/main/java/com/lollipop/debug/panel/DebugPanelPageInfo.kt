package com.lollipop.debug.panel

import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

sealed class DebugPanelPageInfo {
    abstract val id: String
    abstract val name: String

    class RemoteStatic(val info: DebugStaticPanelPageInfo) : DebugPanelPageInfo() {
        override val id: String = info.id
        override val name: String = info.name
    }

    class RemoteList(val info: DebugListPanelPageInfo) : DebugPanelPageInfo() {
        override val id: String = info.name
        override val name: String = info.name
    }

}