package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugPanelPageStaticBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo

class DebugStaticPagerHolder(
    val binding: DebugPanelPageStaticBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener {

    init {
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
    }

    private val contentGroup: LinearLayout
        get() {
            return binding.itemGroup
        }

    private var currentPanelInfo: DebugStaticPanelPageInfo? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        binding.refreshLayout.isRefreshing = false
        currentPanelInfo?.let {
            buildContent(it)
        }
    }

    fun bind(info: DebugPanelPageDescriptor.RemoteStatic) {
        buildContent(info.info)
    }

    private fun buildContent(info: DebugStaticPanelPageInfo) {
        // TODO
    }

}