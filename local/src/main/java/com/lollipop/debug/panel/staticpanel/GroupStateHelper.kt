package com.lollipop.debug.panel.staticpanel

class GroupStateHelper {

    companion object {
        private val panelMap = GroupStateHelper()

        fun panel(key: String): GroupStateHelper {
            return panelMap.getOrCreate(key)
        }

    }

    private val childMap = mutableMapOf<String, GroupStateHelper>()
    var isOpen = false

    private fun getOrCreate(key: String): GroupStateHelper {
        return childMap.getOrPut(key) { GroupStateHelper() }
    }

    fun child(key: String, index: Int): GroupStateHelper {
        return getOrCreate(key.ifEmpty { createDefaultKey(index) })
    }

    private fun createDefaultKey(index: Int): String {
        return "Group_$index"
    }

}