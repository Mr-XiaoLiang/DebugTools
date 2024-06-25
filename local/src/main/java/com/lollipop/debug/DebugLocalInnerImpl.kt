package com.lollipop.debug

import android.app.Activity
import android.app.Application
import com.lollipop.debug.basic.DebugLocalBasicImpl

object DebugLocalInnerImpl: DebugLocalBasicImpl() {
    override fun onInit(app: Application) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
    }

}