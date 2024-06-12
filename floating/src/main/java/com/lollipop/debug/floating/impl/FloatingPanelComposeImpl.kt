package com.lollipop.debug.floating.impl

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

internal class FloatingPanelComposeImpl(
    context: Context,
    private val composeContent: @Composable () -> Unit
) : BasicFloatingPanel(context) {
    override fun createPanelContent(context: Context): View {
        return ComposeView(context).apply {
            setParentCompositionContext(null)
            setContent(composeContent)
        }
    }

}