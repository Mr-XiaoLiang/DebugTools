package com.lollipop.debug

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.Keep
import com.lollipop.debug.local.R

@Keep
object DebugLocal {

    var isOverlayMode = false

    var floatingButtonIcon = R.mipmap.debug_ic_fab

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

    @Keep
    @JvmStatic
    fun init(application: Application) {
        if (isOverlayMode) {
            DebugLocalOverlayImpl.init(application)
        } else {
            DebugLocalInnerImpl.init(application)
        }
    }

    @Keep
    @JvmStatic
    fun getMetaString(context: Context, key: String): String {
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

    @Keep
    @JvmStatic
    fun getMetaBoolean(context: Context, key: String, def: Boolean): Boolean {
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

}