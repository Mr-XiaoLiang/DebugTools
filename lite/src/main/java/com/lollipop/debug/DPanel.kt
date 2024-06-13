package com.lollipop.debug

object DPanel {

    var implements: DebugPanel? = null

    fun getPanel(name: String): DebugPanelView {
        return implements?.getPanel(name) ?: EmptyPanelView
    }

    interface DebugPanel {
        fun getPanel(name: String): DebugPanelView
    }

    interface DebugPanelView {
        fun group(id: String, name: String)
        fun button(id: String, groupId: String, name: String, onClickListener: () -> Unit)
        fun text(id: String, groupId: String, name: String, value: String)
    }

    private object EmptyPanelView : DebugPanelView {
        override fun group(id: String, name: String) {
        }

        override fun button(
            id: String,
            groupId: String,
            name: String,
            onClickListener: () -> Unit
        ) {
        }

        override fun text(id: String, groupId: String, name: String, value: String) {
        }
    }

}