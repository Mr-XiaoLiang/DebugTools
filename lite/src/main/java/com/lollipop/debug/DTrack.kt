package com.lollipop.debug

import com.lollipop.debug.track.TrackAction

object DTrack {

    var implements: DebugTrack? = null

    fun log(
        action: TrackAction,
        pageName: String,
        targetName: String,
        sourcePage: String,
        message: String,
        data: Map<String, String>
    ) {
        implements?.log(action, pageName, targetName, sourcePage, message, data)
    }

    interface DebugTrack {

        fun log(
            action: TrackAction,
            pageName: String,
            targetName: String,
            sourcePage: String,
            message: String,
            data: Map<String, String>
        )

    }

}