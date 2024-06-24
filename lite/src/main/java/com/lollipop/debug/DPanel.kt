package com.lollipop.debug

import com.lollipop.debug.panel.DebugListPanelAdapter
import com.lollipop.debug.panel.DebugListPanelPage
import com.lollipop.debug.panel.DebugStaticPanelPage
import com.lollipop.debug.panel.EmptyDebugListPanelPage
import com.lollipop.debug.panel.EmptyStaticPanelView

object DPanel {

    var implements: DebugPanel? = null

    fun panel(name: String): DebugStaticPanelPage {
        return implements?.panel(name) ?: EmptyStaticPanelView
    }

    fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage {
        return implements?.list(name, adapter) ?: EmptyDebugListPanelPage
    }

    fun navigate(name: String) {
        implements?.navigate(name)
    }

    interface DebugPanel {
        fun panel(name: String): DebugStaticPanelPage

        fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage

        fun navigate(name: String)
    }

}