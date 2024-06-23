package com.lollipop.debug.panel

import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

sealed class DebugPanelPageDescriptor {
    abstract val closeable: Boolean
    abstract val id: String

    data object Toast : DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = "TOAST_HISTORY"
    }

    class RemoteStatic(val info: DebugStaticPanelPageInfo) : DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = info.id
    }

    class RemoteList(val info: DebugListPanelPageInfo): DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = info.name

    }

}