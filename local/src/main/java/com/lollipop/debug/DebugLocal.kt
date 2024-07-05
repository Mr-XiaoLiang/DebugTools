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
import com.lollipop.debug.panel.DebugPanelImpl

@Keep
object DebugLocal {

    @Keep
    @JvmStatic
    var panelMode: PanelMode = PanelMode.AUTO
        private set

    @Keep
    @JvmStatic
    var floatingButtonIcon = R.mipmap.debug_ic_fab

    const val KEY_REQUEST_OVERLAY_PERMISSION = "com.lollipop.debug.overlay.request"

    @Keep
    @JvmStatic
    fun hasOverlayPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    @Keep
    @JvmStatic
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

    private fun checkPanelMode(application: Application): PanelMode {
        if (PanelMode.AUTO == panelMode) {
            panelMode = if (hasOverlayPermission(application)) {
                PanelMode.OVERLAY
            } else {
                PanelMode.INNER
            }
            return panelMode
        }
        return panelMode
    }

    @Keep
    @JvmStatic
    fun setPanelInnerMode() {
        if (panelMode != PanelMode.AUTO) {
            return
        }
        panelMode = PanelMode.INNER
    }

    @Keep
    @JvmStatic
    fun setPanelOverlayMode() {
        if (panelMode != PanelMode.AUTO) {
            return
        }
        panelMode = PanelMode.OVERLAY
    }

    @Keep
    @JvmStatic
    fun init(application: Application) {
        DToast.implements = DebugToastHelper
        DPanel.implements = DebugPanelImpl
        if (checkPanelMode(application) == PanelMode.OVERLAY) {
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

    enum class PanelMode {
        AUTO,
        INNER,
        OVERLAY
    }

}