package com.lollipop.debug.helper

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class ListenerManager<T> {

    private val listenerList = CopyOnWriteArrayList<WeakReference<T>>()

    fun invoke(callback: (T) -> Unit) {
        listenerList.forEach {
            it.get()?.let(callback)
        }
    }

    fun add(listener: T) {
        if (listenerList.any { it.get() === listener }) {
            return
        }
        listenerList.add(WeakReference(listener))
    }

    fun remove(listener: T) {
        val removed = HashSet<WeakReference<T>>()
        for (item in listenerList) {
            val t = item.get()
            if (t == null || t === listener) {
                removed.add(item)
            }
        }
        listenerList.removeAll(removed)
    }

    fun clear() {
        listenerList.clear()
    }

}