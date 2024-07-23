package com.lollipop.debug.track

import androidx.lifecycle.Lifecycle
import java.util.LinkedList

object DTrackManager {

    val trackPageStack = LinkedList<DTrackPage>()

    fun createPage(pageName: String, lifecycle: Lifecycle? = null): DTrackPage {
        val page = DTrackPage(pageName, trackPageStack.lastOrNull())
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

}