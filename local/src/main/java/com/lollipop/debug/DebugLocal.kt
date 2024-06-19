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

    fun hasOverlayPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun init(application: Application) {
        isRequestOverlayPermission = getMetaBoolean(
            application, KEY_REQUEST_OVERLAY_PERMISSION, false
        )
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
        if (hasOverlayPermission(activity)) {
            initDebugLocal(activity)
            return
        }
        if (isRequestOverlayPermission) {
            requestOverlayPermission(activity)
            // 请求过一次，不管给不给，都不要再请求了
            isRequestOverlayPermission = false
        }
    }

    private fun initDebugLocal(activity: Activity) {
        // TODO
        DebugToastHelper.init(activity.application)
        InitState.isToastInit = true
    }

    private fun getMetaBoolean(context: Context, key: String, def: Boolean): Boolean {
        try {
            val metaData = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            ).metaData
            if (metaData.containsKey(key)) {
                return metaData.getBoolean(key, def)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return def
    }

    private fun getMetaString(context: Context, key: String): String {
        try {
            val metaData = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            ).metaData
            if (metaData.containsKey(key)) {
                return metaData.getString(key) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
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