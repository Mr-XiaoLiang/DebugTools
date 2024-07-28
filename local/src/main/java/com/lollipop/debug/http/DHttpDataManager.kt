package com.lollipop.debug.http

import com.lollipop.debug.basic.DebugBasicDataManager
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.http.DHttpImpl
import com.lollipop.debug.core.http.DHttpInfo

data object DHttpDataManager : DebugBasicDataManager<DHttpInfo>() {

    override fun query(
        currentMinTime: Long,
        currentPageSize: Int,
        currentPageIndex: Int
    ): ListResult<DHttpInfo> {
        return DHttpImpl.queryOriginal(currentMinTime, currentPageSize, currentPageIndex)
    }

}