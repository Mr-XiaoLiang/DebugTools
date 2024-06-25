package com.lollipop.debug.toast

import java.text.SimpleDateFormat
import java.util.Date

class ToastInfo(
    val text: String,
    val detail: String,
    val time: Long
) {

    private var timeTextValue: String? = null

    fun getTimeText(sdf: SimpleDateFormat): String {
        if (timeTextValue != null) {
            return timeTextValue!!
        }
        val value = sdf.format(Date(time))
        timeTextValue = value
        return value
    }

}