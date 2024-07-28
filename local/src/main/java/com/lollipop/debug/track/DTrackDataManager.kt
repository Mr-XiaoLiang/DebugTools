package com.lollipop.debug.track

import com.lollipop.debug.basic.DebugBasicDataManager
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.track.DTrackImpl
import com.lollipop.debug.core.track.DTrackInfo

data object DTrackDataManager : DebugBasicDataManager<DTrackInfo>() {

    override fun query(
        currentMinTime: Long,
        currentPageSize: Int,
        currentPageIndex: Int
    ): ListResult<DTrackInfo> {
        return DTrackImpl.queryOriginal(currentMinTime, currentPageSize, currentPageIndex)
    }

}