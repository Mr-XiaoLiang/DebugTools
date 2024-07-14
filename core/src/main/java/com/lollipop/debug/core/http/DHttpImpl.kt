package com.lollipop.debug.core.http

import com.lollipop.debug.DHttp
import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType

object DHttpImpl: DHttp.DebugHttp {
    override fun log(
        type: RequestType,
        state: HttpState,
        url: String,
        time: Long,
        header: Map<String, String>,
        data: String,
        response: String,
    ) {
        TODO("Not yet implemented")
    }
}