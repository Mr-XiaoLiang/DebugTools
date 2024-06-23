package com.lollipop.debug.panel.staticpanel

class DebugStaticButtonItemInfo(
    id: String,
    name: String,
    var onClickListener: () -> Unit
) : DebugStaticPanelItemInfo(id, name)

