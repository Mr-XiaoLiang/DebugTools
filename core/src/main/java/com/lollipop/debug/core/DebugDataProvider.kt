package com.lollipop.debug.core

interface DebugDataProvider<T : Any> {

    fun select(pageSize: Int, pageIndex: Int): List<T>

}