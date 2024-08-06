package com.lollipop.debug.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.core.track.DTrackInfo
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryBinding

class TrackHistoryAdapter(
    val data: List<DTrackInfo>,
    val onItemClick: (DTrackInfo) -> Unit
) : RecyclerView.Adapter<DTrackItemHolder.Header>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DTrackItemHolder.Header {
        return DTrackItemHolder.Header(
            DebugItemTrackHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            ::onItemClick
        )
    }

    private fun onItemClick(index: Int) {
        if (index < 0 || index >= data.size) {
            return
        }
        onItemClick(data[index])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DTrackItemHolder.Header, position: Int) {
        holder.bind(data[position])
    }

}