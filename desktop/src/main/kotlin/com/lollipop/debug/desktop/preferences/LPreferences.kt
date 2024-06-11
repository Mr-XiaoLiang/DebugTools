package com.lollipop.debug.desktop.preferences

interface LPreferences {

    fun put(key: String, value: String)

    fun put(key: String, value: Int)

    fun put(key: String, value: Boolean)

    fun put(key: String, value: Float)

    fun put(key: String, value: Long)

    fun getString(key: String, def: String): String

    fun getInt(key: String, def: Int): Int

    fun getBoolean(key: String, def: Boolean): Boolean

    fun getFloat(key: String, def: Float): Float

    fun getLong(key: String, def: Long): Long

}