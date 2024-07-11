package com.lollipop.debug.core.http

import com.lollipop.debug.http.RequestType

class DHttpInfo(
    val type: RequestType,
    val url: String,
    val header: Map<String, String>,
    val data: String,
    val response: String
)