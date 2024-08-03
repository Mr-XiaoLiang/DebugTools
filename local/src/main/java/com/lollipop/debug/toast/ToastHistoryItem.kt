package com.lollipop.debug.toast

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.DToast
import com.lollipop.debug.local.databinding.DebugItemToastHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ToastHistoryItem(val binding: DebugItemToastHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        binding.toastHistoryTime.setOnLongClickListener {
            onTimeLongClick()
        }
        binding.toastHistoryTitle.setOnLongClickListener {
            onTitleLongClick()
        }
        binding.toastHistoryDetail.setOnLongClickListener {
            onInfoLongClick()
        }
    }

    private fun onTimeLongClick(): Boolean {
        copyInfo(binding.toastHistoryTime.text?.toString() ?: "")
        return true
    }

    private fun onTitleLongClick(): Boolean {
        copyInfo(binding.toastHistoryTitle.text?.toString() ?: "")
        return true
    }

    private fun onInfoLongClick(): Boolean {
        copyInfo(binding.toastHistoryDetail.text?.toString() ?: "")
        return true
    }

    private fun copyInfo(info: String) {
        val context = itemView.context
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE)
        if (clipboardManager is ClipboardManager) {
            //添加ClipData对象到剪切板中
            clipboardManager.setPrimaryClip(ClipData.newPlainText(info, info))
            DToast.show("copy: $info")
        }
    }

    fun bind(info: ToastInfo) {
        binding.toastHistoryTitle.text = info.text
        binding.toastHistoryDetail.text = info.detail
        binding.toastHistoryTime.text = info.getTimeText(sdf)
    }

}