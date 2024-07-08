package com.lollipop.debug.lite

abstract class LiteProxy<T : Any> {

    protected val implementsList = ArrayList<T>()

    fun register(implements: T) {
        if (implements == this) {
            // 禁止循环添加自己
            return
        }
        if (implementsList.contains(implements)) {
            return
        }
        this.implementsList.add(implements)
    }

    fun unregister(implements: T) {
        this.implementsList.remove(implements)
    }

    protected fun invoke(block: (T) -> Unit) {
        for (implements in implementsList) {
            block(implements)
        }
    }

}