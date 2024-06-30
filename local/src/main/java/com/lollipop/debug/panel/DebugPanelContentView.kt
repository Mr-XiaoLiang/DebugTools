package com.lollipop.debug.panel

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lollipop.debug.local.databinding.DebugPanelContentBinding

class DebugPanelContentView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet), DebugPanelImpl.OnPageChangedListener {

    private val binding = DebugPanelContentBinding.inflate(LayoutInflater.from(context), this)
    private val tabLayout: TabLayout
        get() {
            return binding.tabLayout
        }
    private val viewPager: ViewPager2
        get() {
            return binding.viewPager2
        }

    private val dataList = DebugPanelImpl.pages
    private val adapter = DebugPanelPagerAdapter(dataList)

    init {
        orientation = VERTICAL
        initView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resume() {
        DebugPanelImpl.addListener(this)
        adapter.notifyDataSetChanged()
    }

    fun pause() {
        DebugPanelImpl.removeListener(this)
    }

    fun destroy() {
        DebugPanelImpl.removeListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        DebugPanelImpl.removeListener(this)
    }

    private fun initView() {
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val descriptor = dataList[position]
            val customView = tab.customView
            if (customView is DebugPanelTabView) {
                customView.setTabInfo(tab, descriptor.label, descriptor.closeable)
            } else {
                val newTabView = DebugPanelTabView(context)
                newTabView.setTabInfo(tab, descriptor.label, descriptor.closeable)
                tab.customView = newTabView
            }
        }.attach()
    }

    override fun onPageAdd(index: Int) {
        adapter.notifyItemInserted(index)
    }

    override fun onPageRemove(index: Int) {
        adapter.notifyItemRemoved(index)
    }

}