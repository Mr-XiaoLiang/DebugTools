package com.lollipop.debug.panel

import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

sealed class DebugPanelPageInfo {
    abstract val id: String

    class RemoteStatic(val info: DebugStaticPanelPageInfo) : DebugPanelPageInfo() {
        override val id: String = info.id
    }

    class RemoteList(val info: DebugListPanelPageInfo) : DebugPanelPageInfo() {
        override val id: String = info.name
    }

}