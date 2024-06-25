package com.lollipop.debug.panel

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DebugPanelContentView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet), DebugPanelImpl.OnPageChangedListener {

    private val tabLayout = TabLayout(context)
    private val viewPager = ViewPager2(context)

    private val dataList = DebugPanelImpl.pages
    private val adapter = DebugPanelPagerAdapter(dataList)

    init {
        orientation = VERTICAL
        addView(tabLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        initView()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun resume() {
        DebugPanelImpl.addListener(this)
        adapter.notifyDataSetChanged()
    }

    fun destroy() {
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