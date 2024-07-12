package com.lollipop.debug.core.track

import android.content.Context
import com.lollipop.debug.core.DebugDataProvider
import com.lollipop.debug.core.base.BasicDebugDataProvider

class DTrackSelector(context: Context) : BasicDebugDataProvider<DTrackInfo>(context) {

    override fun onSelect(pageSize: Int, pageIndex: Int): List<DTrackInfo> {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }
}