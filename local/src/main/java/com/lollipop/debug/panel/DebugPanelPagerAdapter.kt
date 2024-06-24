package com.lollipop.debug.panel

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.panel.pager.DebugPagerHolder

class DebugPanelPagerAdapter(val data: List<DebugPanelPageDescriptor>) :
    RecyclerView.Adapter<DebugPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebugPagerHolder {
        when {
            viewType == DebugPanelPageDescriptor.Main.itemType -> {
                // main page
            }

            viewType == DebugPanelPageDescriptor.Toast.itemType -> {
                // toast page
            }

            viewType >= DebugPanelPageDescriptor.LIST_PAGE_RANGE -> {
                // list page
            }

            else -> {
                // static page
            }
        }
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DebugPagerHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemType
    }

}