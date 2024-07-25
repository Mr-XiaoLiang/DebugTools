package com.lollipop.debug.panel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.local.databinding.DebugPanelPageHomeBinding
import com.lollipop.debug.local.databinding.DebugPanelPageListBinding
import com.lollipop.debug.local.databinding.DebugPanelPageStaticBinding
import com.lollipop.debug.local.databinding.DebugPanelPageToastHistoryBinding
import com.lollipop.debug.panel.pager.DebugHomePagerHolder
import com.lollipop.debug.panel.pager.DebugListPagerHolder
import com.lollipop.debug.panel.pager.DebugPagerHolder
import com.lollipop.debug.panel.pager.DebugStaticPagerHolder
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
                return DebugListPagerHolder(
                    DebugPanelPageListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                // static page
                return DebugStaticPagerHolder(
                    DebugPanelPageStaticBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DebugPagerHolder, position: Int) {
        val info = data[position]
        when (holder) {
            is DebugListPagerHolder -> {
                if (info is DebugPanelPageDescriptor.RemoteList) {
                    holder.bind(info)
                }
            }

            is DebugStaticPagerHolder -> {
                if (info is DebugPanelPageDescriptor.RemoteStatic) {
                    holder.bind(info)
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: DebugPagerHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: DebugPagerHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemType
    }

}