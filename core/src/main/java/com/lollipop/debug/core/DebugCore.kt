package com.lollipop.debug.core

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.debug.DHttp
import com.lollipop.debug.DTrack
import com.lollipop.debug.core.base.BasicDebugDataProvider
import com.lollipop.debug.core.http.DHttpImpl
import com.lollipop.debug.core.http.DHttpInfo
import com.lollipop.debug.core.http.DHttpSelector
import com.lollipop.debug.core.track.DTrackImpl
import com.lollipop.debug.core.track.DTrackInfo
import com.lollipop.debug.core.track.DTrackSelector

@Keep
object DebugCore {

    @Keep
    @JvmStatic
    fun init(application: Application) {
        DHttp.register(DHttpImpl)
        DTrack.register(DTrackImpl)
    }

    fun trackSelector(context: Context, lifecycle: Lifecycle): DebugDataProvider<DTrackInfo> {
        return LifecycleWrapper(lifecycle, DTrackSelector(context))
    }

    fun httpSelector(context: Context, lifecycle: Lifecycle): DebugDataProvider<DHttpInfo> {
        return LifecycleWrapper(lifecycle, DHttpSelector(context))
    }

    private class LifecycleWrapper<T : Any>(
        lifecycle: Lifecycle,
        private val provider: BasicDebugDataProvider<T>
    ) : LifecycleEventObserver, DebugDataProvider<T> {

        private var isActive = true

        init {
            lifecycle.addObserver(this)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    isActive = false
                    provider.destroy()
                    source.lifecycle.removeObserver(this)
                }

                else -> {}
            }
        }

        override fun select(pageSize: Int, pageIndex: Int): List<T> {
            if (!isActive) {
                return emptyList()
            }
            return provider.onSelect(pageSize, pageIndex)
        }


    }

}