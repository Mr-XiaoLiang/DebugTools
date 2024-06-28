package com.lollipop.debug.panel.staticpanel

class DebugStaticPanelButtonItemInfo(
    id: String,
    name: String,
    var onClickListener: () -> Unit
) : DebugStaticPanelItemInfo(id, name)

