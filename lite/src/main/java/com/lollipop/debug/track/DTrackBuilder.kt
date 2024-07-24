package com.lollipop.debug.track

import com.lollipop.debug.DTrack
import org.json.JSONObject

abstract class DTrackBuilder(
    val pageName: String,
    val sourceName: String,
) {

    val data = mutableMapOf<String, String>()
    var targetName: String = ""
    var message: String = ""
    var customExtra: String = ""
    val jsonExtra = JSONObject()

    protected fun cloneFrom(builder: DTrackBuilder) {
        data.putAll(builder.data)
        targetName = builder.targetName
        message = builder.message
        customExtra = builder.customExtra
        try {
            val jsonInfo = builder.jsonExtra.toString()
            if (jsonInfo.isNotEmpty()) {
                val newJson = JSONObject(jsonInfo)
                newJson.keys().forEach {
                    jsonExtra.put(it, newJson.opt(it))
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    protected fun getExtra(): String {
        return customExtra.ifEmpty { jsonExtra.toString() }
    }

    protected fun createSnapshot(action: TrackAction): Snapshot {
        val d = HashMap<String, String>()
        d.putAll(data)
        return Snapshot(
            action = action,
            pageName = pageName,
            sourceName = sourceName,
            data = d,
            targetName = targetName,
            message = message,
            extra = getExtra()
        )
    }

    protected fun track(snapshot: Snapshot) {
        DTrack.log(
            action = snapshot.action,
            pageName = snapshot.pageName,
            targetName = snapshot.targetName,
            sourcePage = snapshot.sourceName,
            message = snapshot.message,
            data = snapshot.data,
            extra = snapshot.extra
        )
    }

    protected fun track(action: TrackAction) {
        track(createSnapshot(action))
    }

    class Snapshot(
        val action: TrackAction,
        val pageName: String,
        val sourceName: String,
        val data: Map<String, String>,
        var targetName: String,
        var message: String,
        var extra: String
    )

}