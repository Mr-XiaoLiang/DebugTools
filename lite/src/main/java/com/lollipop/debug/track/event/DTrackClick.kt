package com.lollipop.debug.track.event

import com.lollipop.debug.track.DTrackPage
import com.lollipop.debug.track.TrackAction

class DTrackClick(
    target: String,
    page: DTrackPage,
) : DTrackBasicEvent(target, page, TrackAction.Click)