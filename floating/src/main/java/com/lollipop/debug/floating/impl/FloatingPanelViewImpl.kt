package com.lollipop.debug.floating.impl

import android.content.Context
import android.view.View

internal class FloatingPanelViewImpl(
    context: Context,
    private val contentView: View
) : BasicFloatingPanel(context) {
    override fun createPanelContent(context: Context): View {
        return contentView
    }

}