package com.lollipop.debug.floating

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.Window
import android.view.WindowManager
import com.lollipop.debug.floating.creator.FloatingInnerCreator
import com.lollipop.debug.floating.creator.FloatingOverlayCreator

object FloatingHelper {

    fun overlay(application: Application): FloatingOverlayCreator {
        return FloatingOverlayCreator(application)
    }

    fun inner(activity: Activity): FloatingInnerCreator {
        return FloatingInnerCreator(activity)
    }

    fun getScreenSize(windowManager: WindowManager): Size {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            return Size(windowMetrics.bounds.width(), windowMetrics.bounds.height())
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    fun getWindowSize(window: Window): Size {
        val view = window.decorView
        return Size(view.width, view.height)
    }

    fun flagsOf(vararg flags: Int): Int {
        var result = 0
        for (flag in flags) {
            result = result or flag
        }
        return result
    }

}