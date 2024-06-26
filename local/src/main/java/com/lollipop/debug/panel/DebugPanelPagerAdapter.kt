package com.lollipop.debug.panel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.local.databinding.DebugPanelPageHomeBinding
import com.lollipop.debug.local.databinding.DebugPanelPageToastHistoryBinding
import com.lollipop.debug.panel.pager.DebugHomePagerHolder
import com.lollipop.debug.panel.pager.DebugPagerHolder
import com.lollipop.debug.panel.pager.DebugToastHistoryPagerHolder

class DebugPanelPagerAdapter(val data: List<DebugPanelPageDescriptor>) :
    RecyclerView.Adapter<DebugPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebugPagerHolder {
        when {
            viewType == DebugPanelPageDescriptor.Main.itemType -> {
                // main page
                return DebugHomePagerHolder(
                    DebugPanelPageHomeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == DebugPanelPageDescriptor.Toast.itemType -> {
                // toast page
                return DebugToastHistoryPagerHolder(
                    DebugPanelPageToastHistoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType >= DebugPanelPageDescriptor.LIST_PAGE_RANGE -> {
                // list page
                TODO("Not yet implemented")
            }

            else -> {
                // static page
                TODO("Not yet implemented")
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DebugPagerHolder, position: Int) {
        when (holder) {

        }
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemType
    }

}