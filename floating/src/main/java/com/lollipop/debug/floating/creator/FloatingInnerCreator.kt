package com.lollipop.debug.floating.creator

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import com.lollipop.debug.floating.FloatingButton
import com.lollipop.debug.floating.FloatingPanel
import com.lollipop.debug.floating.FloatingResult

class FloatingInnerCreator(val activity: Activity): FloatingCreator() {

    override fun createPanel(
        config: FloatingPanelConfig,
        composeContent: @Composable () -> Unit
    ): FloatingResult<FloatingPanel> {
        TODO("Not yet implemented")
    }

    override fun createPanel(
        config: FloatingPanelConfig,
        content: View
    ): FloatingResult<FloatingPanel> {
        TODO("Not yet implemented")
    }

    override fun createButton(
        config: FloatingButtonConfig,
        button: View
    ): FloatingResult<FloatingButton> {
        TODO("Not yet implemented")
    }

    override fun createIcon(
        config: FloatingButtonConfig,
        iconId: Int,
        onClickListener: View.OnClickListener
    ): FloatingResult<FloatingButton> {
        TODO("Not yet implemented")
    }

    fun addView(
        view: View,
        builder: (FrameLayout.LayoutParams) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}