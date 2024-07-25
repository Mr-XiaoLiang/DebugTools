package com.lollipop.debug.panel.pager

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class DebugPagerHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun onAttached()

    abstract fun onDetached()

}