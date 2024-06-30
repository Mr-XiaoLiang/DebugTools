package com.lollipop.debug.floating.creator

import android.view.View
import android.view.View.OnClickListener
import androidx.compose.runtime.Composable
import com.lollipop.debug.floating.FloatingButton
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.FloatingResult

abstract class FloatingCreator {

    abstract fun createPanel(
        config: FloatingPanelConfig = FloatingPanelConfig.DEFAULT,
        composeContent: @Composable () -> Unit
    ): FloatingResult<FloatingPanel>

    abstract fun createPanel(
        config: FloatingPanelConfig = FloatingPanelConfig.DEFAULT,
        content: View
    ): FloatingResult<FloatingPanel>

    abstract fun createButton(
        config: FloatingButtonConfig = FloatingButtonConfig.DEFAULT,
        button: View,
    ): FloatingResult<FloatingButton>

    abstract fun createIcon(
        config: FloatingButtonConfig = FloatingButtonConfig.DEFAULT,
        iconId: Int,
        onClickListener: OnClickListener
    ): FloatingResult<FloatingButton>

}