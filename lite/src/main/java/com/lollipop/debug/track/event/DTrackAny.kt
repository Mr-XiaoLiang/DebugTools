package com.lollipop.debug.track.event

import com.lollipop.debug.track.DTrackPage
import com.lollipop.debug.track.TrackAction

class DTrackAny(
    target: String,
    page: DTrackPage,
    action: String
) : DTrackBasicEvent(target, page, TrackAction.parse(action))