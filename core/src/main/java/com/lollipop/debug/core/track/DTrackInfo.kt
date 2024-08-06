package com.lollipop.debug.core.track

import com.lollipop.debug.track.TrackAction
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DTrackInfo(
    val id: Long = NO_ID,
    val action: TrackAction,
    val pageName: String,
    val targetName: String,
    val sourcePage: String,
    val message: String,
    val data: Map<String, String>,
    val extra: String,
    val time: Long
) {
    companion object {
        const val NO_ID = -1L

        fun parseJson(json: String): Map<String, String> {
            try {
                return parseJson(JSONObject(json))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return emptyMap()
        }

        fun parseJson(jsonObject: JSONObject): Map<String, String> {
            val map = mutableMapOf<String, String>()
            try {
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = jsonObject.optString(key)
                    map[key] = value
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return map
        }

    }

    val timeValue: String by lazy {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        sdf.format(Date(time))
    }

    fun dataToJson(): String {
        return dataToJsonObj().toString()
    }

    fun dataToJsonObj(): JSONObject {
        val json = JSONObject()
        data.forEach {
            json.put(it.key, it.value)
        }
        return json
    }

}