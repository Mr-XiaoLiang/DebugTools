package com.lollipop.debug.core.track

import com.lollipop.debug.track.TrackAction

class DTrackInfo(
    val action: TrackAction,
    val pageName: String,
    val targetName: String,
    val sourcePage: String,
    val message: String,
    val data: Map<String, String>
)