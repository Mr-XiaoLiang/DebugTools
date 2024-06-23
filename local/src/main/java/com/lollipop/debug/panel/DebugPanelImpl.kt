package com.lollipop.debug.panel

import com.lollipop.debug.DPanel
import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

data object DebugPanelImpl : DPanel.DebugPanel {

    private val pageList = ArrayList<DebugPanelPageDescriptor>()

    val pages: List<DebugPanelPageDescriptor>
        get() {
            return pageList
        }

    override fun panel(name: String): DebugStaticPanelPage {
        for (info in pageList) {
            if (info is DebugPanelPageDescriptor.RemoteStatic && info.id == name) {
                return info.info
            }
        }
        val info = DebugStaticPanelPageInfo(name, name)
        pageList.add(DebugPanelPageDescriptor.RemoteStatic(info))
        return info
    }

    override fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage {
        for (info in pageList) {
            if (info is DebugPanelPageDescriptor.RemoteList && info.id == name) {
                return info.info
            }
        }
        val info = DebugListPanelPageInfo(name, adapter)
        pageList.add(DebugPanelPageDescriptor.RemoteList(info))
        return info
    }

    fun push(descriptor: DebugPanelPageDescriptor) {
        pageList.add(descriptor)
    }

    fun remove(descriptor: DebugPanelPageDescriptor) {
        pageList.remove(descriptor)
    }

    fun remove(id: String) {
        val removed = HashSet<DebugPanelPageDescriptor>()
        for (info in pageList) {
            if (info.closeable && info.id == id) {
                removed.add(info)
            }
        }
        pageList.removeAll(removed)
    }

}





