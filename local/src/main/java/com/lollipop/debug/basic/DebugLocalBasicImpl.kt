package com.lollipop.debug.basic

import android.app.Activity
import android.app.Application
import android.os.Bundle

abstract class DebugLocalBasicImpl : Application.ActivityLifecycleCallbacks {

    protected var application: Application? = null

    fun init(app: Application) {
        if (application != null) {
            return
        }
        application = app
        app.registerActivityLifecycleCallbacks(this)
        onInit(app)
    }

    protected abstract fun onInit(app: Application)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}