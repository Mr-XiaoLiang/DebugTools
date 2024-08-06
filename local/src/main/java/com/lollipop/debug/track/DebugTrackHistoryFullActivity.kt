package com.lollipop.debug.track

import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.basic.DebugSearchListBasicActivity
import com.lollipop.debug.core.track.DTrackInfo

class DebugTrackHistoryFullActivity : DebugSearchListBasicActivity<DTrackInfo>() {
    override val loadMoreEnable: Boolean = true

    override fun getSearchResultAsync(
        key: String,
        pageSize: Int,
        pageIndex: Int,
        callback: (List<DTrackInfo>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun createAdapter(dataList: List<DTrackInfo>): RecyclerView.Adapter<*> {
        TODO("Not yet implemented")
    }

}