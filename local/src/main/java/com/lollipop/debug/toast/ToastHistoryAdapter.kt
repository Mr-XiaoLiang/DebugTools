package com.lollipop.debug.toast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.local.databinding.DebugItemToastHistoryBinding

class ToastHistoryAdapter(
    val data: List<ToastInfo>
) : RecyclerView.Adapter<ToastHistoryItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToastHistoryItem {
        return ToastHistoryItem(
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

    override fun onBindViewHolder(holder: ToastHistoryItem, position: Int) {
        holder.bind(data[position])
    }

}