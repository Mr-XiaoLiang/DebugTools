package com.lollipop.debug.core

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import com.lollipop.debug.DHttp
import com.lollipop.debug.DTrack
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
        // TODO()
        return DTrackSelector(context)
    }

    fun httpSelector(context: Context, lifecycle: Lifecycle): DebugDataProvider<DHttpInfo> {
        return DHttpSelector(context)
    }

}