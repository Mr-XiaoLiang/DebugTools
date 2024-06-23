package com.lollipop.debug.panel.staticpanel

import com.lollipop.debug.panel.DebugStaticPanelGroup
import java.util.LinkedList


abstract class BasicDebugStaticGroupInfo(
    id: String,
    name: String,
) : DebugStaticPanelItemInfo(id, name), DebugStaticPanelGroup {

    protected val currentId: String
        get() {
            return id
        }

    val itemList = ArrayList<DebugStaticPanelItemInfo>()

    abstract fun onItemChanged()

    abstract fun createGroup(id: String, name: String): BasicDebugStaticGroupInfo

    private inline fun <reified T : DebugStaticPanelItemInfo> findItem(id: String): T? {
        val pendingList = LinkedList<DebugStaticPanelItemInfo>()
        pendingList.add(this)
        while (pendingList.isNotEmpty()) {
            val item = pendingList.removeFirst()
            if (item is T && item.id == id) {
                return item
            }
            if (item is BasicDebugStaticGroupInfo) {
                pendingList.addAll(item.itemList)
            }
        }
        return null
    }

    private fun optGroup(id: String, name: String?): BasicDebugStaticGroupInfo {
        val group = findItem<BasicDebugStaticGroupInfo>(id)
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

    override fun group(id: String, name: String): DebugStaticPanelGroup {
        return optGroup(id, name)
    }

    override fun button(
        id: String,
        groupId: String,
        name: String,
        onClickListener: () -> Unit
    ) {
        if (groupId.isEmpty() || groupId == currentId) {
            val buttonItem = findItem<DebugStaticButtonItemInfo>(id)
            if (buttonItem != null) {
                buttonItem.onClickListener = onClickListener
                buttonItem.name = name
                onItemChanged()
                return
            }
            val newButtonItem = DebugStaticButtonItemInfo(id, name, onClickListener)
            itemList.add(newButtonItem)
            onItemChanged()
            return
        }
        optGroup(id, null).button(id, groupId, name, onClickListener)
    }

    override fun text(id: String, groupId: String, name: String, value: String) {
        if (groupId.isEmpty() || groupId == currentId) {
            val textItem = findItem<DebugStaticTextItemInfo>(id)
            if (textItem != null) {
                textItem.value = value
                textItem.name = name
                onItemChanged()
                return
            }
            val newButtonItem = DebugStaticTextItemInfo(id, name, value)
            itemList.add(newButtonItem)
            onItemChanged()
            return
        }
        optGroup(id, null).text(id, groupId, name, value)
    }

}