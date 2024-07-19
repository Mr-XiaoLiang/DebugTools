package com.lollipop.debug

import com.lollipop.debug.lite.LiteProxy
import com.lollipop.debug.track.TrackAction

object DTrack : LiteProxy<DTrack.DebugTrack>() {

    fun log(
        action: TrackAction,
        pageName: String,
        targetName: String,
        sourcePage: String,
        message: String,
        data: Map<String, String>,
        extra: String
    ) {
        invoke { it.log(action, pageName, targetName, sourcePage, message, data, extra) }
    }

    interface DebugTrack {

        fun log(
            action: TrackAction,
            pageName: String,
            targetName: String,
            sourcePage: String,
            message: String,
            data: Map<String, String>,
            extra: String
        )

    }

}