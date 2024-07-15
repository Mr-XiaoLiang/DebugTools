package com.lollipop.debug.core.http

import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType
import org.json.JSONObject

class DHttpInfo(
    val id: Long = NO_ID,
    val type: RequestType,
    val state: HttpState,
    val url: String,
    val time: Long,
    val header: Map<String, String>,
    val data: String,
    val response: String,
) {

    companion object {
        const val NO_ID = -1L

        fun parseHeader(json: String): Map<String, String> {
            try {
                if (json.isEmpty()) {
                    return emptyMap()
                }
                val result = HashMap<String, String>()
                val jsonObject = JSONObject(json)
                jsonObject.keys().forEach { key ->
                    val value = jsonObject.optString(key)
                    result[key] = value
                }
                return result
            } catch (e: Exception) {
                return emptyMap()
            }
        }

        fun formatHeader(header: Map<String, String>): String {
            try {
                val json = JSONObject()
                header.keys.forEach { key ->
                    json.put(key, header[key])
                }
                return json.toString()
            } catch (e: Throwable) {
                return ""
            }
        }

    }

}