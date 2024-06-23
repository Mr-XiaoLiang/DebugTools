package com.lollipop.debug.panel.listpanel

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.panel.DebugListPanelAdapter
import com.lollipop.debug.panel.DebugListPanelItem

class DebugListPanelPageAdapterImpl(
    private val list: List<DebugListPanelItem>,
    private val adapterCallback: DebugListPanelAdapter
) : RecyclerView.Adapter<DebugListPanelPageItemImpl>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebugListPanelPageItemImpl {
        return DebugListPanelPageItemImpl(adapterCallback.onCreateView(parent, viewType))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DebugListPanelPageItemImpl, position: Int) {
        adapterCallback.onBindView(holder.itemView, list[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].itemType
    }

}