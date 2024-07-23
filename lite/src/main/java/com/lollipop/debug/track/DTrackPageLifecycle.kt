package com.lollipop.debug.track

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class DTrackPageLifecycle(
    val page: DTrackPage,
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                pageResume()
            }

            Lifecycle.Event.ON_PAUSE -> {
                pagePause()
            }

            else -> {
            }
        }
    }

    fun flush(lifecycle: Lifecycle) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            pageResume()
        } else {
            pagePause()
        }
    }

    private fun pageResume() {
        if (page.isResumed) {
            return
        }
        page.onResume()
    }

    private fun pagePause() {
        if (!page.isResumed) {
            return
        }
        page.onPause()
    }

}