package com.lollipop.debug

import com.lollipop.debug.http.RequestType
import com.lollipop.debug.lite.LiteProxy

object DHttp : LiteProxy<DHttp.DebugHttp>() {

    fun log(
        type: RequestType,
        url: String,
        header: Map<String, String>,
        data: String,
        response: String
    ) {
        invoke { it.log(type, url, header, data, response) }
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