package com.lollipop.debug.track

import com.lollipop.debug.DTrack
import org.json.JSONObject

open class DTrackPage(
    val pageName: String,
    val source: DTrackPage?,
) {

    val data = mutableMapOf<String, String>()
    var targetName: String = ""
    var message: String = ""
    var customExtra: String = ""
    val jsonExtra = JSONObject()

    protected var beforeResume: BeforePageResume? = null
    protected var afterResume: AfterPageResume? = null
    protected var beforePause: BeforePagePause? = null
    protected var afterPause: AfterPagePause? = null

    var isResumed = false
        protected set

    private fun getExtra(): String {
        return customExtra.ifEmpty { jsonExtra.toString() }
    }

    open fun beforeResume(block: BeforePageResume): DTrackPage {
        beforeResume = block
        return this
    }

    open fun afterResume(block: AfterPageResume): DTrackPage {
        afterResume = block
        return this
    }

    open fun beforePause(block: BeforePagePause): DTrackPage {
        beforePause = block
        return this
    }

    open fun afterPause(block: AfterPagePause): DTrackPage {
        afterPause = block
        return this
    }

    open fun onResume() {
        beforeResume?.onBeforeResume(this)
        isResumed = true
        DTrack.log(
            action = TrackAction.PageShow,
            pageName = pageName,
            targetName = targetName,
            sourcePage = source?.pageName ?: "",
            message = message,
            data = data,
            extra = getExtra()
        )
        afterResume?.onAfterResume(this)
    }

    open fun onPause() {
        beforePause?.onBeforePause(this)
        isResumed = false
        DTrack.log(
            action = TrackAction.PageHide,
            pageName = pageName,
            targetName = targetName,
            sourcePage = source?.pageName ?: "",
            message = message,
            data = data,
            extra = getExtra()
        )
        afterPause?.onAfterPause(this)
    }

    fun interface BeforePageResume {
        fun onBeforeResume(page: DTrackPage)
    }

    fun interface BeforePagePause {
        fun onBeforePause(page: DTrackPage)
    }

    fun interface AfterPageResume {
        fun onAfterResume(page: DTrackPage)
    }

    fun interface AfterPagePause {
        fun onAfterPause(page: DTrackPage)
    }

    interface PageCallback {
        fun onPageResume(page: DTrackPage)
        fun onPagePause(page: DTrackPage)
    }

}