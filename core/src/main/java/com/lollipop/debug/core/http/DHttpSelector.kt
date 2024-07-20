package com.lollipop.debug.core.http

import android.content.Context
import com.lollipop.debug.core.base.BasicDebugDataProvider
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.base.StaticResult

class DHttpSelector(context: Context) : BasicDebugDataProvider<DHttpInfo>(context) {

    val dbHelper = DHttpDataService(context)

    override fun onSelect(pageSize: Int, pageIndex: Int): List<DHttpInfo> {
        val result = dbHelper.queryLimit(0, pageSize, pageIndex)
        if (result is ListResult.Success<DHttpInfo>) {
            return result.data
        }
        return emptyList()
    }

    override fun onDestroy() {
        dbHelper.close()
    }

    fun insert(info: DHttpInfo): StaticResult<Unit> {
        return dbHelper.insert(info)
    }

}