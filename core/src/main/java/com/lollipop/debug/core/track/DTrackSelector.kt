package com.lollipop.debug.core.track

import android.content.Context
import com.lollipop.debug.core.base.BasicDebugDataProvider
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.base.StaticResult

class DTrackSelector(context: Context) : BasicDebugDataProvider<DTrackInfo>(context) {

    val dbHelper = DTrackDataService(context)

    override fun onSelect(pageSize: Int, pageIndex: Int): List<DTrackInfo> {
        val result = dbHelper.queryLimit(0, pageSize, pageIndex)
        if (result is ListResult.Success<DTrackInfo>) {
            return result.data
        }
        return emptyList()
    }

    override fun onDestroy() {
        dbHelper.close()
    }

    fun insert(info: DTrackInfo): StaticResult<Unit> {
        return dbHelper.insert(info)
    }

}