package com.lollipop.debug.core.track

import com.lollipop.debug.DTrack
import com.lollipop.debug.track.TrackAction

object DTrackImpl : DTrack.DebugTrack {
    override fun log(
        action: TrackAction,
        pageName: String,
        targetName: String,
        sourcePage: String,
        message: String,
        data: Map<String, String>,
        extra: String
    ) {
        TODO("Not yet implemented")
    }
}