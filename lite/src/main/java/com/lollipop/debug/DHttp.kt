package com.lollipop.debug

import com.lollipop.debug.http.RequestType

object DHttp {

    var implements: DebugHttp? = null

    fun log(
        type: RequestType,
        url: String,
        header: Map<String, String>,
        data: String,
        response: String
    ) {
        implements?.log(type, url, header, data, response)
    }

    interface DebugHttp {
        fun log(
            type: RequestType,
            url: String,
            header: Map<String, String>,
            data: String,
            response: String
        )

    }

}