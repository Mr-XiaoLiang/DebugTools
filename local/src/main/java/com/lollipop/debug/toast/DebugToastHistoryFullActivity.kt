package com.lollipop.debug.toast

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.DebugToastHelper
import com.lollipop.debug.basic.DebugSearchListBasicActivity

class DebugToastHistoryFullActivity : DebugSearchListBasicActivity<ToastInfo>() {

    companion object {
        fun start(context: Context) {
            start(context, DebugToastHistoryFullActivity::class.java)
        }
    }

    override val loadMoreEnable: Boolean = false

    override fun getSearchResultAsync(
        key: String,
        pageSize: Int,
        pageIndex: Int,
        callback: (List<ToastInfo>) -> Unit
    ) {
        val allList = ArrayList<ToastInfo>()
        allList.addAll(DebugToastHelper.toastHistory)
        if (key.isEmpty()) {
            callback(allList)
            return
        }
        val result = ArrayList<ToastInfo>()
        allList.forEach {
            if (it.text.contains(key) || it.detail.contains(key)) {
                result.add(it)
            }
        }
        callback(result)
    }

    override fun createAdapter(dataList: List<ToastInfo>): RecyclerView.Adapter<*> {
        return ToastHistoryAdapter(dataList)
    }

}