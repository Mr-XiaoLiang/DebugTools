package com.lollipop.debug

import com.lollipop.debug.panel.DebugListPanelAdapter
import com.lollipop.debug.panel.DebugListPanelPage
import com.lollipop.debug.panel.DebugStructuredPanelPage
import com.lollipop.debug.panel.EmptyDebugListPanelPage
import com.lollipop.debug.panel.EmptyStructuredPanelView

object DPanel {

    var implements: DebugPanel? = null

    fun panel(name: String): DebugStructuredPanelPage {
        return implements?.panel(name) ?: EmptyStructuredPanelView
    }

    fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage {
        return implements?.list(name, adapter) ?: EmptyDebugListPanelPage
    }

    interface DebugPanel {
        fun panel(name: String): DebugStructuredPanelPage

        fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage
    }

}