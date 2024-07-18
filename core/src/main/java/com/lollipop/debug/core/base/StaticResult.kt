package com.lollipop.debug.core.base

sealed class StaticResult<T> {
    class Success<T>(val data: T) : StaticResult<T>()
    class Error<T>(val exception: Throwable) : StaticResult<T>()
    class Empty<T> : StaticResult<T>()
}

sealed class ListResult<T> {
    class Success<T>(val data: List<T>) : ListResult<T>()
    class Error<T>(val exception: Throwable) : ListResult<T>()
}