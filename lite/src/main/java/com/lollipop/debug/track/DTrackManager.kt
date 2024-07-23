package com.lollipop.debug.track

import androidx.lifecycle.Lifecycle
import java.util.LinkedList

object DTrackManager : DTrackPage.PageCallback {

    private val EMPTY_PAGE = DTrackPage("", null, this)

    val trackPageStack = LinkedList<DTrackPage>()

    fun createPage(pageName: String, lifecycle: Lifecycle? = null): DTrackPage {
        val page = DTrackPage(pageName, trackPageStack.lastOrNull(), this)
        if (lifecycle != null) {
            bindLifecycle(page, lifecycle)
        }
        return page
    }

    fun bindLifecycle(page: DTrackPage, lifecycle: Lifecycle) {
        val lifecycleObserver = DTrackPageLifecycle(page)
        lifecycle.addObserver(lifecycleObserver)
        lifecycleObserver.flush(lifecycle)
    }

    override fun onPageResume(page: DTrackPage) {
        trackPageStack.addLast(page)
    }

    override fun onPagePause(page: DTrackPage) {
        trackPageStack.remove(page)
    }

    private fun getCurrentPage(): DTrackPage {
        return if (trackPageStack.isEmpty()) {
            EMPTY_PAGE
        } else {
            trackPageStack.last
        }
    }

}