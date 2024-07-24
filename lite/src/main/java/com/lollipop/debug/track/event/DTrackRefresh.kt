package com.lollipop.debug.track.event

import com.lollipop.debug.track.DTrackPage
import com.lollipop.debug.track.TrackAction

class DTrackRefresh(
    target: String,
    page: DTrackPage,
) : DTrackBasicEvent(target, page, TrackAction.Refresh)