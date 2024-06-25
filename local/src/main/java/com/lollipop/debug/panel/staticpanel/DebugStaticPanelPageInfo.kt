package com.lollipop.debug.panel.staticpanel

import com.lollipop.debug.panel.DebugStaticPanelPage

class DebugStaticPanelPageInfo(
    id: String,
    name: String,
) : BasicDebugStaticGroupInfo(id, name), DebugStaticPanelPage {

    var changeMode = 0
        private set

    override fun onItemChanged() {
        changeMode++
        onPageChangedListener?.onPageChanged()
    }

    var onPageChangedListener: OnPageChangedListener? = null

    override fun createGroup(id: String, name: String): BasicDebugStaticGroupInfo {
        return DebugStaticPanelGroupInfo(id, name) { onItemChanged() }
    }

    interface OnPageChangedListener {
        fun onPageChanged()
    }

}