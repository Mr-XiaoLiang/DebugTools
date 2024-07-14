package com.lollipop.debug

import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType
import com.lollipop.debug.lite.LiteProxy

object DHttp : LiteProxy<DHttp.DebugHttp>() {

    fun log(
        type: RequestType,
        state: HttpState,
        url: String,
        time: Long,
        header: Map<String, String>,
        data: String,
        response: String,
    ) {
        invoke { it.log(type, state, url, time, header, data, response) }
    }

    interface DebugHttp {
        fun log(
            type: RequestType,
            state: HttpState,
            url: String,
            time: Long,
            header: Map<String, String>,
            data: String,
            response: String,
        )

    }

}