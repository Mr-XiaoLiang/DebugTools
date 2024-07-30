package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.DebugToastHelper
import com.lollipop.debug.basic.DebugBasicHistoryPagerHolder
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugItemToastHistoryBinding
import com.lollipop.debug.local.databinding.DebugPanelPageBasicHistoryBinding
import com.lollipop.debug.toast.ToastInfo
import java.text.SimpleDateFormat
import java.util.Locale

class DebugToastHistoryPagerHolder(
    context: Context
) : DebugBasicHistoryPagerHolder<ToastInfo>(
    DebugPanelPageBasicHistoryBinding.inflate(
        LayoutInflater.from(context)
    )
), SwipeRefreshLayout.OnRefreshListener {

    private val currentToastData = ArrayList<ToastInfo>()

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ToastAdapter(currentToastData)
    }

    override fun createOptions(): List<Option> {
        return listOf(
            option(
                title = R.string.refresh,
            ) {
                callRefresh()
            }
        )
    }

    override fun canLoadMore(): Boolean {
        return false
    }

    override fun callLoadMore() {
        // nothing
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        syncData()
        notifyDataSetChanged()
        onRefreshEnd()
    }

    private fun syncData() {
        currentToastData.clear()
        currentToastData.addAll(DebugToastHelper.toastHistory)
    }

    private class ToastAdapter(
        val data: List<ToastInfo>
    ) : RecyclerView.Adapter<ToastItem>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToastItem {
            return ToastItem(
                DebugItemToastHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ToastItem, position: Int) {
            holder.bind(data[position])
        }

    }

    private class ToastItem(val binding: DebugItemToastHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(info: ToastInfo) {
            binding.toastHistoryTitle.text = info.text
            binding.toastHistoryDetail.text = info.detail
            binding.toastHistoryTime.text = info.getTimeText(sdf)
        }

    }

    override fun onAttached() {
    }

    override fun onDetached() {
    }

}