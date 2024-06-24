package com.lollipop.debug.panel

import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

sealed class DebugPanelPageDescriptor {
    abstract val closeable: Boolean
    abstract val id: String
    abstract val label: String
    abstract val itemType: Int

    companion object {

        const val LIST_PAGE_RANGE = Int.MAX_VALUE / 2

        private var staticPageIndex = 0
        private var listPageIndex = LIST_PAGE_RANGE

        private fun nextStaticIndex(): Int {
            if (staticPageIndex >= LIST_PAGE_RANGE) {
                throw IllegalStateException("Too many static pages")
            }
            return staticPageIndex++
        }

        private fun nextListIndex(): Int {
            if (listPageIndex >= Int.MAX_VALUE) {
                throw IllegalStateException("Too many list pages")
            }
            return listPageIndex++
        }

    }

    data object Toast : DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = "TOAST_HISTORY"
        override val label: String = "Toast"
        override val itemType: Int = -2

    }

    data object Main : DebugPanelPageDescriptor() {
        override val closeable: Boolean = false
        override val id: String = "PAGE_MAIN"
        override val label: String = "Home"
        override val itemType: Int = -1
    }

    class RemoteStatic(val info: DebugStaticPanelPageInfo) : DebugPanelPageDescriptor() {
        override val closeable: Boolean = true
        override val id: String = info.id
        override val label: String = info.name
        override val itemType: Int = nextStaticIndex()
    }

    class RemoteList(val info: DebugListPanelPageInfo) : DebugPanelPageDescriptor() {
        override val closeable: Boolean = true
        override val id: String = info.name
        override val label: String = info.name
        override val itemType: Int = nextListIndex()
    }

}