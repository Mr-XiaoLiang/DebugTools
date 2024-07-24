package com.lollipop.debug.track

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.lollipop.debug.track.event.DTrackAny
import com.lollipop.debug.track.event.DTrackBackground
import com.lollipop.debug.track.event.DTrackClick
import com.lollipop.debug.track.event.DTrackForeground
import com.lollipop.debug.track.event.DTrackLaunch
import com.lollipop.debug.track.event.DTrackRefresh
import java.util.LinkedList

object DTrackManager : DTrackPage.PageCallback {

    private val EMPTY_PAGE = DTrackPage("", null, this)

    @JvmStatic
    val trackPageStack = LinkedList<DTrackPage>()

    @JvmStatic
    @JvmOverloads
    fun createPage(pageName: String, lifecycle: Lifecycle? = null): DTrackPage {
        val page = DTrackPage(pageName, trackPageStack.lastOrNull(), this)
        if (lifecycle != null) {
            bindLifecycle(page, lifecycle)
        }
        return page
    }

    @JvmStatic
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


    @JvmStatic
    @JvmOverloads
    fun launch(target: String = "", builder: (DTrackLaunch) -> Unit = {}) {
        getCurrentPage().launch(target = target, builder = builder)
    }

    @JvmStatic
    @JvmOverloads
    fun foreground(target: String = "", builder: (DTrackForeground) -> Unit = {}) {
        getCurrentPage().foreground(target = target, builder = builder)
    }

    @JvmStatic
    @JvmOverloads
    fun background(target: String = "", builder: (DTrackBackground) -> Unit = {}) {
        getCurrentPage().background(target = target, builder = builder)
    }

    @JvmStatic
    @JvmOverloads
    fun click(target: String = "", builder: (DTrackClick) -> Unit = {}) {
        getCurrentPage().click(target = target, builder = builder)
    }

    @JvmStatic
    @JvmOverloads
    fun refresh(target: String = "", builder: (DTrackRefresh) -> Unit = {}) {
        getCurrentPage().refresh(target = target, builder = builder)
    }

    @JvmStatic
    @JvmOverloads
    fun trackAny(action: String, target: String = "", builder: (DTrackAny) -> Unit = {}) {
        getCurrentPage().trackAny(action = action, target = target, builder = builder)
    }

}

fun LifecycleOwner.registerTrackPage(pageName: String): DTrackPage {
    return DTrackManager.createPage(pageName, lifecycle)
}
