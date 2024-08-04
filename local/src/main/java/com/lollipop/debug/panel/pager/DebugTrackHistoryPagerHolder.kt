package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.DTrack
import com.lollipop.debug.DebugToastHelper
import com.lollipop.debug.basic.DebugBasicHistoryPagerHolder
import com.lollipop.debug.core.track.DTrackInfo
import com.lollipop.debug.local.R
import com.lollipop.debug.panel.DebugPanelPageDescriptor
import com.lollipop.debug.toast.DebugToastHistoryFullActivity
import com.lollipop.debug.toast.ToastHistoryAdapter
import com.lollipop.debug.toast.ToastInfo

class DebugTrackHistoryPagerHolder(
    parent: ViewGroup
) : DebugBasicHistoryPagerHolder<DTrackInfo>(parent) {

    private val currentToastData = ArrayList<DTrackInfo>()

    override fun createAdapter(): RecyclerView.Adapter<*> {

        TODO()

    }

    override fun createOptions(): List<Option> {
        return listOf(
            option(
                title = R.string.detail,
            ) {
                openDetail()
            }
        )
    }

    override fun canLoadMore(): Boolean {
        return false
    }

    override fun callLoadMore() {
        // nothing
    }

    private fun openDetail() {
        DebugToastHistoryFullActivity.start(itemView.context)
    }

    override fun onBind(info: DebugPanelPageDescriptor) {
        super.onBind(info)
        if (currentToastData.isEmpty()) {
            callRefresh()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        syncData()
        notifyDataSetChanged()
        onRefreshEnd()
    }

    private fun syncData() {
        TODO()
    }

    override fun onAttached() {
    }

    override fun onDetached() {
    }

}