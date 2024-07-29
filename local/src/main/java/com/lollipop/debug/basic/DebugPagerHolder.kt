package com.lollipop.debug.basic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.panel.DebugPanelPageDescriptor

abstract class DebugPagerHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun onBind(info: DebugPanelPageDescriptor)

    abstract fun onAttached()

    abstract fun onDetached()

}