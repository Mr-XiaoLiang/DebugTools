package com.lollipop.debug.http

sealed class RequestType(val type: String) {

    data object GET : RequestType("GET")
    data object POST : RequestType("POST")
    data object PUT : RequestType("PUT")
    data object DELETE : RequestType("DELETE")
    data object HEAD : RequestType("HEAD")
    data object OPTIONS : RequestType("OPTIONS")
    data object PATCH : RequestType("PATCH")
    data object TRACE : RequestType("TRACE")
    data object CONNECT : RequestType("CONNECT")
    class OTHER(type: String) : RequestType(type)

}