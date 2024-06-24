package com.lollipop.debug.panel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import com.lollipop.debug.local.databinding.DebugItemPageTabBinding

class DebugPanelTabView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val binding = DebugItemPageTabBinding.inflate(LayoutInflater.from(context), this, true)

    private val tabBackground by lazy {
        ResourcesCompat.getDrawable(
            resources,
            com.lollipop.debug.floating.R.drawable.l_debug_bg_floating_button,
            context.theme
        )
    }

    private var tab: TabLayout.Tab? = null

    init {
        binding.tabCloseButton.setOnClickListener {
            onCloseButtonClick()
        }
    }

    fun setTabInfo(tab: TabLayout.Tab, label: String, closeable: Boolean) {
        binding.tabCloseButton.isVisible = closeable
        binding.tabLabelView.text = label
        this.tab = tab
    }

    private fun onCloseButtonClick() {
        val position = tab?.position ?: return
        DebugPanelImpl.remove(position)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        binding.root.background = if (selected) {
            tabBackground
        } else {
            null
        }
    }

}