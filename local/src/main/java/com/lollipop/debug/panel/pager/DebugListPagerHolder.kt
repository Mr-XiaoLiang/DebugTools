package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.helper.RecyclerViewLoadMoreHelper
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugPanelPageListBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor
import com.lollipop.debug.panel.listpanel.DebugListPanelPageInfo

class DebugListPagerHolder(
    val binding: DebugPanelPageListBinding
) : DebugPagerHolder(binding.root),
    SwipeRefreshLayout.OnRefreshListener,
    DebugListPanelPageInfo.OnResetCallback,
    DebugListPanelPageInfo.OnAppendCallback {

    private var listPage: DebugListPanelPageInfo? = null

    private val loadMoreHelper = RecyclerViewLoadMoreHelper(
        loadMoreStateProvider = {
            canLoadMore()
        },
        loadMoreListener = {
            callLoadMore()
        },
    )

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
        loadMoreHelper.attach(binding.recyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        val page = listPage
        if (page == null || !page.canRefresh()) {
            binding.refreshLayout.isRefreshing = false
            return
        }
        page.refresh()
    }

    fun bind(info: DebugPanelPageDescriptor.RemoteList) {
        val page = info.info
        onPageChanged(page)
        binding.recyclerView.adapter = page.adapter
    }

    private fun onPageChanged(newPage: DebugListPanelPageInfo) {
        onDetached()
        listPage = newPage
        onAttached()
    }

    override fun onAppend(page: DebugListPanelPageInfo) {
        if (listPage !== page) {
            return
        }
        loadMoreHelper.isLoading = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onReset(page: DebugListPanelPageInfo) {
        if (listPage !== page) {
            return
        }
        binding.refreshLayout.isRefreshing = false
        loadMoreHelper.isLoading = false
    }

    override fun onAttached() {
        listPage?.let { page ->
            page.registerOnReset(this)
            page.registerOnAppend(this)
        }
    }

    override fun onDetached() {
        listPage?.let { page ->
            page.unregisterOnReset(this)
            page.unregisterOnAppend(this)
        }
    }

    private fun callLoadMore() {
        listPage?.loadMore()
    }

    private fun canLoadMore(): Boolean {
        if (binding.refreshLayout.isRefreshing) {
            return false
        }
        return listPage?.canLoadMore() ?: false
    }

}