package com.lollipop.debug.core.track

import android.annotation.SuppressLint
import android.app.Application
import com.lollipop.debug.DTrack
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.track.TrackAction

object DTrackImpl : DTrack.DebugTrack {

    @SuppressLint("StaticFieldLeak")
    private var dataService: DTrackSelector? = null

    fun init(application: Application) {
        dataService = DTrackSelector(application)
        DTrack.register(this)
    }

    fun query(minTime: Long = 0L, pageSize: Int, pageIndex: Int): List<DTrackInfo> {
        val result = dataService?.dbHelper?.queryLimit(
            minTime, pageSize, pageIndex
        ) ?: return emptyList()
        if (result is ListResult.Success<DTrackInfo>) {
            return result.data
        }
        return emptyList()
    }


    override fun log(
        action: TrackAction,
        pageName: String,
        targetName: String,
        sourcePage: String,
        message: String,
        data: Map<String, String>,
        extra: String
    ) {
        dataService?.insert(
            DTrackInfo(
                action = action,
                pageName = pageName,
                targetName = targetName,
                sourcePage = sourcePage,
                message = message,
                data = data,
                extra = extra,
                time = System.currentTimeMillis()
            )
        )
    }
}