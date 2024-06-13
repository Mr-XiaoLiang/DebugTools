package com.lollipop.debug.track

sealed class TrackAction(val type: String) {
    data object LAUNCH : TrackAction("launch")
    data object FOREGROUND : TrackAction("foreground")
    data object BACKGROUND : TrackAction("background")
    data object CLICK : TrackAction("click")
    data object PAGE_SHOW : TrackAction("page_show")
    data object PAGE_HIDE : TrackAction("page_hide")
    data object REFRESH : TrackAction("refresh")
    class OTHER(type: String) : TrackAction(type)
}