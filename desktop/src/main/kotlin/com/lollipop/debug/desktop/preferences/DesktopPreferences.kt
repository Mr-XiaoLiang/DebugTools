package com.lollipop.debug.desktop.preferences

import java.util.prefs.Preferences

class DesktopPreferences(
    private val sp: Preferences
) : LPreferences {

    override fun put(key: String, value: String) {
        sp.put(key, value)
    }

    override fun put(key: String, value: Int) {
        sp.putInt(key, value)
    }

    override fun put(key: String, value: Boolean) {
        sp.putBoolean(key, value)
    }

    override fun put(key: String, value: Float) {
        sp.putFloat(key, value)
    }

    override fun put(key: String, value: Long) {
        sp.putLong(key, value)
    }

    override fun getString(key: String, def: String): String {
        return sp.get(key, def) ?: def
    }

    override fun getInt(key: String, def: Int): Int {
        return sp.getInt(key, def)
    }

    override fun getBoolean(key: String, def: Boolean): Boolean {
        return sp.getBoolean(key, def)
    }

    override fun getFloat(key: String, def: Float): Float {
        return sp.getFloat(key, def)
    }

    override fun getLong(key: String, def: Long): Long {
        return sp.getLong(key, def)
    }

}