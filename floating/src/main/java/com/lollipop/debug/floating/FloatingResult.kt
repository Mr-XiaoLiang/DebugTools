package com.lollipop.debug.floating

sealed class FloatingResult<T : FloatingCloseable> : FloatingCloseable {

    abstract fun getOrNull(): T?

    abstract val isSuccess: Boolean

    class Failure<T : FloatingCloseable>(val error: Throwable) : FloatingResult<T>() {
        override fun getOrNull(): T? {
            return null
        }

        override val isSuccess: Boolean = false

        override fun closeFloating() {
        }
    }

    class Overlay<T : FloatingCloseable>(val view: T) : FloatingResult<T>() {
        override fun getOrNull(): T {
            return view
        }

        override val isSuccess: Boolean = true

        override fun closeFloating() {
            view.closeFloating()
        }
    }

    class Inner<T : FloatingCloseable>(val view: T) : FloatingResult<T>() {
        override fun getOrNull(): T {
            return view
        }

        override val isSuccess: Boolean = true

        override fun closeFloating() {
            view.closeFloating()
        }
    }

}