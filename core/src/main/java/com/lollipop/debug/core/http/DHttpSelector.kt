package com.lollipop.debug.core.http

import android.content.Context
import com.lollipop.debug.core.DebugDataProvider
import com.lollipop.debug.core.base.BasicDebugDataProvider

class DHttpSelector(context: Context) : BasicDebugDataProvider<DHttpInfo>(context) {

    override fun onSelect(pageSize: Int, pageIndex: Int): List<DHttpInfo> {
        // TODO("Not yet implemented")
        return emptyList()
    }

    override fun onDestroy() {
        // TODO("Not yet implemented")
    }

}