package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugPanelPageListBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor

class DebugListPagerHolder(
    val binding: DebugPanelPageListBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener {

    init {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            binding.root.context, RecyclerView.VERTICAL, false
        )
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        binding.recyclerView.adapter?.notifyDataSetChanged()
        binding.refreshLayout.isRefreshing = false
    }

    fun bind(info: DebugPanelPageDescriptor.RemoteList) {
        binding.recyclerView.adapter = info.info.adapter
    }

}