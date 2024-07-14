package com.lollipop.debug.core.http

import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType

class DHttpInfo(
    val type: RequestType,
    val state: HttpState,
    val url: String,
    val time: Long,
    val header: Map<String, String>,
    val data: String,
    val response: String,
)