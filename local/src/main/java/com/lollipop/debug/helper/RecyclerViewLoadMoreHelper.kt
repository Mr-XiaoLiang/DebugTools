package com.lollipop.debug.helper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewLoadMoreHelper(
    val keepCount: Int = 3,
    var loadMoreStateProvider: LoadMoreStateProvider,
    var loadMoreListener: LoadMoreListener,
) : RecyclerView.OnScrollListener() {

    var isLoading = false

    fun attach(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (isLoading) {
            return
        }
        if (dy <= 0) {
            return
        }
        if (!canLoadMore()) {
            return
        }
        val itemCount = recyclerView.adapter?.itemCount ?: return
        val layoutManager = recyclerView.layoutManager ?: return
        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val positionArray = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(positionArray)
                positionArray.forEach { pos ->
                    if (pos >= (itemCount - keepCount)) {
                        loadMore()
                        return
                    }
                }
            }

            is GridLayoutManager -> {
                val position = layoutManager.findLastVisibleItemPosition()
                if (position >= (itemCount - keepCount)) {
                    loadMore()
                    return
                }
            }

            is LinearLayoutManager -> {
                val position = layoutManager.findLastVisibleItemPosition()
                if (position >= (itemCount - keepCount)) {
                    loadMore()
                    return
                }
            }
        }
    }

    private fun loadMore() {
        if (isLoading) {
            return
        }
        isLoading = true
        loadMoreListener.loadMore()
    }

    fun canLoadMore(): Boolean {
        return loadMoreStateProvider.canLoadMore() ?: false
    }

    fun interface LoadMoreStateProvider {
        fun canLoadMore(): Boolean
    }

    fun interface LoadMoreListener {
        fun loadMore()
    }

}