package com.lollipop.debug.basic

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.helper.RecyclerViewLoadMoreHelper
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugPanelPageBasicHistoryBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor

abstract class DebugBasicHistoryPagerHolder<T>(
    val binding: DebugPanelPageBasicHistoryBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener {

    protected var adapter: RecyclerView.Adapter<*>? = null
    protected var isAttached = false
    private var isInitialized = false

    private val loadMoreHelper = RecyclerViewLoadMoreHelper(
        loadMoreStateProvider = {
            canLoadMore()
        },
        loadMoreListener = {
            callLoadMore()
        },
    )

    protected fun initPager() {
        if (isInitialized) {
            return
        }
        if (binding.recyclerView.adapter == null) {
            val ada = adapter ?: createAdapter()
            adapter = ada
            binding.recyclerView.adapter = ada
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager = LinearLayoutManager(
                binding.root.context, RecyclerView.VERTICAL, false
            )
        }
        loadMoreHelper.attach(binding.recyclerView)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
        isInitialized = true
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>

    override fun onBind(info: DebugPanelPageDescriptor) {
        initPager()
        onRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    protected fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }


    protected fun notifyItemRangeInserted(start: Int, count: Int) {
        adapter?.notifyItemRangeInserted(start, count)
    }

    protected fun onRefreshEnd() {
        binding.refreshLayout.isRefreshing = false
    }

    protected fun onLoadMoreEnd() {
        loadMoreHelper.isLoading = false
    }

    protected open fun canLoadMore(): Boolean {
        return isAttached && !binding.refreshLayout.isRefreshing
    }

    protected abstract fun callLoadMore()

    override fun onAttached() {
        isAttached = true
    }

    override fun onDetached() {
        isAttached = false

    }

}