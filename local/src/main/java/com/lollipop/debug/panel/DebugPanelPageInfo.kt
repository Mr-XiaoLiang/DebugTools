package com.lollipop.debug.panel

import com.lollipop.debug.DPanel
import java.util.LinkedList

data object DebugPanelImpl : DPanel.DebugPanel {

    private val pageList = ArrayList<DebugPanelPageDescriptor>()

    val pages: List<DebugPanelPageDescriptor>
        get() {
            return pageList
        }

    override fun panel(name: String): DPanel.DebugPanelPage {
        for (info in pageList) {
            if (info is DebugPanelPageDescriptor.Remote && info.id == name) {
                return info.info
            }
        }
        val info = DebugPanelPageInfo(name, name)
        pageList.add(DebugPanelPageDescriptor.Remote(info))
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

sealed class DebugPanelPageDescriptor {
    abstract val closeable: Boolean
    abstract val id: String

    data object Toast : DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = "TOAST_HISTORY"
    }

    class Remote(val info: DebugPanelPageInfo) : DebugPanelPageDescriptor() {
        override val closeable: Boolean = true
        override val id: String = info.id
    }

}

class DebugPanelPageInfo(
    id: String,
    name: String,
) : BasicDebugGroupInfo(id, name), DPanel.DebugPanelPage {

    var changeMode = 0
        private set

    override fun onItemChanged() {
        changeMode++
    }

    override fun createGroup(id: String, name: String): BasicDebugGroupInfo {
        return DebugPanelGroupInfo(id, name) { onItemChanged() }
    }

}

class DebugPanelGroupInfo(
    id: String,
    name: String,
    private val onChangedCallback: () -> Unit
) : BasicDebugGroupInfo(id, name) {

    override fun onItemChanged() {
        onChangedCallback()
    }

    override fun createGroup(id: String, name: String): BasicDebugGroupInfo {
        return DebugPanelGroupInfo(id, name, onChangedCallback)
    }

}

abstract class BasicDebugGroupInfo(
    id: String,
    name: String,
) : DebugPanelItemInfo(id, name), DPanel.DebugPanelGroup {

    protected val currentId: String
        get() {
            return id
        }

    val itemList = ArrayList<DebugPanelItemInfo>()

    abstract fun onItemChanged()

    abstract fun createGroup(id: String, name: String): BasicDebugGroupInfo

    private inline fun <reified T : DebugPanelItemInfo> findItem(id: String): T? {
        val pendingList = LinkedList<DebugPanelItemInfo>()
        pendingList.add(this)
        while (pendingList.isNotEmpty()) {
            val item = pendingList.removeFirst()
            if (item is T && item.id == id) {
                return item
            }
            if (item is BasicDebugGroupInfo) {
                pendingList.addAll(item.itemList)
            }
        }
        return null
    }

    private fun optGroup(id: String, name: String?): BasicDebugGroupInfo {
        val group = findItem<BasicDebugGroupInfo>(id)
        if (group != null) {
            if (name != null) {
                group.name = name
            }
            onItemChanged()
            return group
        }
        return createGroup(id, name ?: "").also {
            itemList.add(it)
            onItemChanged()
        }
    }

    override fun group(id: String, name: String): DPanel.DebugPanelGroup {
        return optGroup(id, name)
    }

    override fun button(
        id: String,
        groupId: String,
        name: String,
        onClickListener: () -> Unit
    ) {
        if (groupId.isEmpty() || groupId == currentId) {
            val buttonItem = findItem<DebugButtonItemInfo>(id)
            if (buttonItem != null) {
                buttonItem.onClickListener = onClickListener
                buttonItem.name = name
                onItemChanged()
                return
            }
            val newButtonItem = DebugButtonItemInfo(id, name, onClickListener)
            itemList.add(newButtonItem)
            onItemChanged()
            return
        }
        optGroup(id, null).button(id, groupId, name, onClickListener)
    }

    override fun text(id: String, groupId: String, name: String, value: String) {
        if (groupId.isEmpty() || groupId == currentId) {
            val textItem = findItem<DebugTextItemInfo>(id)
            if (textItem != null) {
                textItem.value = value
                textItem.name = name
                onItemChanged()
                return
            }
            val newButtonItem = DebugTextItemInfo(id, name, value)
            itemList.add(newButtonItem)
            onItemChanged()
            return
        }
        optGroup(id, null).text(id, groupId, name, value)
    }

}

class DebugButtonItemInfo(
    id: String,
    name: String,
    var onClickListener: () -> Unit
) : DebugPanelItemInfo(id, name)

class DebugTextItemInfo(
    id: String,
    name: String,
    var value: String
) : DebugPanelItemInfo(id, name)

abstract class DebugPanelItemInfo(
    val id: String,
    var name: String,
)
