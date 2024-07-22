package com.lollipop.debug.core.http

import android.annotation.SuppressLint
import android.app.Application
import com.lollipop.debug.DHttp
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType

object DHttpImpl : DHttp.DebugHttp {

    @SuppressLint("StaticFieldLeak")
    private var dataService: DHttpSelector? = null

    fun init(application: Application) {
        dataService = DHttpSelector(application)
        DHttp.register(this)
    }

    fun query(minTime: Long = 0L, pageSize: Int, pageIndex: Int): List<DHttpInfo> {
        val result = dataService?.dbHelper?.queryLimit(
            minTime, pageSize, pageIndex
        ) ?: return emptyList()
        if (result is ListResult.Success<DHttpInfo>) {
            return result.data
        }
        return emptyList()
    }

    override fun log(
        type: RequestType,
        state: HttpState,
        url: String,
        time: Long,
        header: Map<String, String>,
        data: String,
        response: String,
    ) {
        dataService?.postInsert(
            DHttpInfo(
                type = type,
                state = state,
                url = url,
                time = time,
                header = header,
                data = data,
                response = response
            )
        )
    }
}