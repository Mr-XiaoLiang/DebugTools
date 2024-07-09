package com.lollipop.debug.core

import android.app.Application
import androidx.annotation.Keep
import com.lollipop.debug.DHttp
import com.lollipop.debug.DTrack
import com.lollipop.debug.core.http.DHttpImpl
import com.lollipop.debug.core.track.DTrackImpl

@Keep
object DebugCore {

    @Keep
    @JvmStatic
    fun init(application: Application) {
        DHttp.register(DHttpImpl)
        DTrack.register(DTrackImpl)
    }

}