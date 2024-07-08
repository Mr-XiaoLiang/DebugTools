package com.lollipop.debug

import com.lollipop.debug.lite.LiteProxy
import com.lollipop.debug.panel.DebugListPanelAdapter
import com.lollipop.debug.panel.DebugListPanelPage
import com.lollipop.debug.panel.DebugListPanelPageWrapper
import com.lollipop.debug.panel.DebugStaticPanelPage
import com.lollipop.debug.panel.DebugStaticPanelPageWrapper

object DPanel : LiteProxy<DPanel.DebugPanel>() {

    fun panel(name: String): DebugStaticPanelPage {
        return DebugStaticPanelPageWrapper(implementsList.map { it.panel(name) })
    }

    fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage {
        return DebugListPanelPageWrapper(implementsList.map { it.list(name, adapter) })
    }

    fun navigate(name: String) {
        implementsList.forEach { it.navigate(name) }
    }

    interface DebugPanel {
        fun panel(name: String): DebugStaticPanelPage

        fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage

        fun navigate(name: String)
    }

}