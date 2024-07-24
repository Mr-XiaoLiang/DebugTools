package com.lollipop.debug.track.event

import com.lollipop.debug.track.DTrackPage
import com.lollipop.debug.track.TrackAction

class DTrackForeground(
    target: String,
    page: DTrackPage,
) : DTrackBasicEvent(target, page, TrackAction.Foreground)