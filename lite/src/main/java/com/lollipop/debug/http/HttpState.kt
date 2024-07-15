package com.lollipop.debug.http

import org.json.JSONObject

sealed class HttpState {

    companion object {
        const val TYPE_CODE = "Code"
        const val TYPE_ERROR = "Error"
        const val TYPE_SENDING = "Sending"
        const val TYPE_CANCEL = "Cancel"
        const val TYPE_CUSTOM = "Custom"
        const val TYPE_TIMEOUT = "TimeOut"

        const val KEY_STATE = "state"
        const val KEY_VALUE = "value"

        fun parse(json: String): HttpState {
            if (json.isEmpty()) {
                return Unknown
            }
            return try {
                parse(JSONObject(json))
            } catch (e: Exception) {
                Unknown
            }
        }

        fun parse(json: JSONObject): HttpState {
            val state = json.optString(KEY_STATE) ?: ""
            when (state) {
                TYPE_CODE -> {
                    return Code(json.optInt(KEY_VALUE))
                }

                TYPE_ERROR -> {
                    return Error(json.optString(KEY_VALUE))
                }

                TYPE_SENDING -> {
                    return Sending
                }

                TYPE_CANCEL -> {
                    return Cancel
                }

                TYPE_CUSTOM -> {
                    return Custom(json.optString(KEY_VALUE))
                }

                TYPE_TIMEOUT -> {
                    return TimeOut
                }

                else -> {
                    return Unknown
                }
            }
        }

    }

    fun toJson(): JSONObject {
        return JSONObject().put(KEY_STATE, getType()).put(KEY_VALUE, getValue())
    }

    protected abstract fun getValue(): String
    protected abstract fun getType(): String

    class Code(val code: Int) : HttpState() {
        override fun getValue(): String {
            return code.toString()
        }

        override fun getType(): String {
            return TYPE_CODE
        }

        override fun toString(): String {
            return code.toString()
        }
    }

    data object TimeOut : HttpState() {
        override fun getValue(): String {
            return ""
        }

        override fun getType(): String {
            return TYPE_TIMEOUT
        }

        override fun toString(): String {
            return "TimeOut"
        }
    }

    data object Cancel : HttpState() {
        override fun getValue(): String {
            return ""
        }

        override fun getType(): String {
            return TYPE_CANCEL
        }

        override fun toString(): String {
            return "Cancel"
        }
    }

    class Error(val info: String) : HttpState() {
        override fun getValue(): String {
            return info
        }

        override fun getType(): String {
            return TYPE_ERROR
        }

        override fun toString(): String {
            return info
        }
    }

    data object Sending : HttpState() {
        override fun getValue(): String {
            return ""
        }

        override fun getType(): String {
            return TYPE_SENDING
        }

        override fun toString(): String {
            return "Sending"
        }
    }

    class Custom(val state: String) : HttpState() {
        override fun getValue(): String {
            return state
        }

        override fun getType(): String {
            return TYPE_CUSTOM
        }

        override fun toString(): String {
            return state
        }
    }

    data object Unknown : HttpState() {
        override fun getValue(): String {
            return ""
        }

        override fun getType(): String {
            return ""
        }

        override fun toString(): String {
            return "Unknown"
        }

    }

}