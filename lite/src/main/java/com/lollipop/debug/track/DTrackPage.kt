package com.lollipop.debug.track

import com.lollipop.debug.track.event.DTrackAny
import com.lollipop.debug.track.event.DTrackBackground
import com.lollipop.debug.track.event.DTrackClick
import com.lollipop.debug.track.event.DTrackForeground
import com.lollipop.debug.track.event.DTrackLaunch
import com.lollipop.debug.track.event.DTrackRefresh

open class DTrackPage(
    pageName: String,
    source: DTrackPage?,
    val pageCallback: PageCallback
) : DTrackBuilder(pageName, source?.pageName ?: "") {

    protected var beforeResume: BeforePageResume? = null
    protected var afterResume: AfterPageResume? = null
    protected var beforePause: BeforePagePause? = null
    protected var afterPause: AfterPagePause? = null

    var isResumed = false
        protected set(value) {
            field = value
            data["is_resumed"] = if (value) {
                "true"
            } else {
                "false"
            }
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
        track(TrackAction.PageShow)
        afterResume?.onAfterResume(this)
        pageCallback.onPageResume(this)
    }

    open fun onPause() {
        beforePause?.onBeforePause(this)
        isResumed = false
        track(TrackAction.PageHide)
        afterPause?.onAfterPause(this)
        pageCallback.onPagePause(this)
    }

    @JvmOverloads
    fun launch(target: String = "", builder: (DTrackLaunch) -> Unit = {}) {
        DTrackLaunch(target, this).apply(builder).track()
    }

    @JvmOverloads
    fun foreground(target: String = "", builder: (DTrackForeground) -> Unit = {}) {
        DTrackForeground(target, this).apply(builder).track()
    }

    @JvmOverloads
    fun background(target: String = "", builder: (DTrackBackground) -> Unit = {}) {
        DTrackBackground(target, this).apply(builder).track()
    }

    @JvmOverloads
    fun click(target: String = "", builder: (DTrackClick) -> Unit = {}) {
        DTrackClick(target, this).apply(builder).track()
    }

    @JvmOverloads
    fun refresh(target: String = "", builder: (DTrackRefresh) -> Unit = {}) {
        DTrackRefresh(target, this).apply(builder).track()
    }

    @JvmOverloads
    fun trackAny(action: String, target: String = "", builder: (DTrackAny) -> Unit = {}) {
        DTrackAny(target, this, action).apply(builder).track()
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