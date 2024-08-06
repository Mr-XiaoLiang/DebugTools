package com.lollipop.debug.track

sealed class TrackAction(val type: String) {

    companion object {
        fun parse(type: String): TrackAction {
            return when (type) {
                Launch.type -> Launch
                Foreground.type -> Foreground
                Background.type -> Background
                Click.type -> Click
                PageShow.type -> PageShow
                PageHide.type -> PageHide
                Refresh.type -> Refresh
                else -> Other(type)
            }
        }
    }

    data object Launch : TrackAction("launch")
    data object Foreground : TrackAction("foreground")
    data object Background : TrackAction("background")
    data object Click : TrackAction("click")
    data object PageShow : TrackAction("page_show")
    data object PageHide : TrackAction("page_hide")
    data object Refresh : TrackAction("refresh")
    class Other(type: String) : TrackAction(type)

    val uppercase: String by lazy {
        type.uppercase()
    }
}