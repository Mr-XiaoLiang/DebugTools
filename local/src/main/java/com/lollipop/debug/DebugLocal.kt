package com.lollipop.debug

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings

object DebugLocal {

    private var isRequestOverlayPermission = false

    const val KEY_REQUEST_OVERLAY_PERMISSION = "com.lollipop.debug.overlay.request"

    fun init(application: Application) {
        isRequestOverlayPermission = getMetaDataBoolean(application, KEY_REQUEST_OVERLAY_PERMISSION)
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                checkPermission(activity)
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private fun checkPermission(activity: Activity) {
        if (InitState.isAllInit) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(activity)) {
            initDebugLocal(activity)
            return
        }
        if (isRequestOverlayPermission) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
            // 请求过一次，不管给不给，都不要再请求了
            isRequestOverlayPermission = false
        }
    }

    private fun initDebugLocal(activity: Activity) {
        // TODO
        DebugToastHelper.init(activity.application)
    }

    private fun getMetaDataBoolean(context: Context, key: String): Boolean {
        return getMetaData(context, key).lowercase() == "true"
    }

    private fun getMetaData(context: Context, key: String): String {
        return context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        ).metaData.getString(key) ?: ""
    }

    private object InitState {
        var isPanelInit = false
        var isToastInit = false
        val isAllInit: Boolean
            get() {
                return isPanelInit && isToastInit
            }
    }

}