package com.lollipop.debug.track.event

import com.lollipop.debug.track.DTrackBuilder
import com.lollipop.debug.track.DTrackPage
import com.lollipop.debug.track.TrackAction

abstract class DTrackBasicEvent(
    target: String,
    page: DTrackPage,
    val action: TrackAction
) : DTrackBuilder(page.pageName, page.sourceName) {

    init {
        cloneFrom(page)
        if (target.isNotEmpty()) {
            this.targetName = target
        }
    }

    open fun track() {
        track(action)
    }

}