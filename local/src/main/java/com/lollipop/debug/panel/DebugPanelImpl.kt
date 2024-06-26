package com.lollipop.debug.panel

import com.lollipop.debug.DPanel
import com.lollipop.debug.helper.ListenerManager
import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

data object DebugPanelImpl : DPanel.DebugPanel {

    private val pageMap = HashMap<String, DebugPanelPageInfo>()

    private val pageList = ArrayList<DebugPanelPageDescriptor>()

    private val listenerManager = ListenerManager<OnPageChangedListener>()

    val pages: List<DebugPanelPageDescriptor>
        get() {
            return pageList
        }

    fun getAllPages(): List<DebugPanelPageInfo> {
        return pageMap.values.toList()
    }

    private fun register(descriptor: DebugPanelPageInfo) {
        pageMap[descriptor.id] = descriptor
    }

    override fun panel(name: String): DebugStaticPanelPage {
        val oldInfo = pageMap[name]
        if (oldInfo != null && oldInfo is DebugPanelPageInfo.RemoteStatic) {
            return oldInfo.info
        }
        val info = DebugStaticPanelPageInfo(name, name)
        register(DebugPanelPageInfo.RemoteStatic(info))
        return info
    }

    override fun list(name: String, adapter: DebugListPanelAdapter): DebugListPanelPage {
        val oldInfo = pageMap[name]
        if (oldInfo != null && oldInfo is DebugPanelPageInfo.RemoteList) {
            return oldInfo.info
        }
        val info = DebugListPanelPageInfo(name, adapter)
        register(DebugPanelPageInfo.RemoteList(info))
        return info
    }

    override fun navigate(name: String) {
        val info = pageMap[name] ?: return
        val size = pageList.size
        when (info) {
            is DebugPanelPageInfo.RemoteList -> {
                pageList.add(DebugPanelPageDescriptor.RemoteList(info.info))
            }

            is DebugPanelPageInfo.RemoteStatic -> {
                pageList.add(DebugPanelPageDescriptor.RemoteStatic(info.info))
            }
        }
        if (size < pageList.size) {
            listenerManager.invoke { it.onPageAdd(size) }
        }
    }

    fun remove(index: Int) {
        if (pageList.size > index && index >= 0) {
            val descriptor = pageList[index]
            if (descriptor.closeable) {
                pageList.removeAt(index)
                listenerManager.invoke { it.onPageRemove(index) }
            }
        }
    }

    fun addListener(listener: OnPageChangedListener) {
        listenerManager.add(listener)
    }

    fun removeListener(listener: OnPageChangedListener) {
        listenerManager.remove(listener)
    }

    interface OnPageChangedListener {
        fun onPageAdd(index: Int)
        fun onPageRemove(index: Int)
    }

}





