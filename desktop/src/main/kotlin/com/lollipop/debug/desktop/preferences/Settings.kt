package com.lollipop.debug.desktop.preferences

import java.util.prefs.Preferences
import kotlin.reflect.KProperty

object Settings {

    private val defaultPreferences: LPreferences by lazy {
        DesktopPreferences(Preferences.userRoot().node("wte").node("default"))
    }

    var localLanguage by PreferencesString("")

    private class PreferencesString(private val def: String) {
        operator fun getValue(settings: Settings, property: KProperty<*>): String {
            return defaultPreferences.getString(property.name, def)
        }

        operator fun setValue(settings: Settings, property: KProperty<*>, value: String) {
            defaultPreferences.put(property.name, value)
        }
    }

    private class PreferencesInt(private val def: Int) {
        operator fun getValue(settings: Settings, property: KProperty<*>): Int {
            return defaultPreferences.getInt(property.name, def)
        }

        operator fun setValue(settings: Settings, property: KProperty<*>, value: Int) {
            defaultPreferences.put(property.name, value)
        }
    }

    private class PreferencesBoolean(private val def: Boolean) {
        operator fun getValue(settings: Settings, property: KProperty<*>): Boolean {
            return defaultPreferences.getBoolean(property.name, def)
        }

        operator fun setValue(settings: Settings, property: KProperty<*>, value: Boolean) {
            defaultPreferences.put(property.name, value)
        }
    }

    private class PreferencesLong(private val def: Long) {
        operator fun getValue(settings: Settings, property: KProperty<*>): Long {
            return defaultPreferences.getLong(property.name, def)
        }

        operator fun setValue(settings: Settings, property: KProperty<*>, value: Long) {
            defaultPreferences.put(property.name, value)
        }
    }

    private class PreferencesFloat(private val def: Float) {
        operator fun getValue(settings: Settings, property: KProperty<*>): Float {
            return defaultPreferences.getFloat(property.name, def)
        }

        operator fun setValue(settings: Settings, property: KProperty<*>, value: Float) {
            defaultPreferences.put(property.name, value)
        }
    }


}