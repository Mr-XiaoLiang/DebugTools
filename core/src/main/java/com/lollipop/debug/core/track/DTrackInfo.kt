package com.lollipop.debug.core.track

import com.lollipop.debug.track.TrackAction
import org.json.JSONObject

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
            val map = mutableMapOf<String, String>()
            try {
                val jsonObject = JSONObject(json)
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

    fun dataToJson(): String {
        val json = JSONObject()
        data.forEach {
            json.put(it.key, it.value)
        }
        return json.toString()
    }

}