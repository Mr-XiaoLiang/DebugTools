package com.lollipop.debug.panel

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DebugPanelContentView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet) {

    private val tabLayout = TabLayout(context)
    private val viewPager = ViewPager2(context)

    private val dataList = DebugPanelImpl.pages

    init {
        orientation = VERTICAL
        addView(tabLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        initView()
    }

    private fun initView() {
//        viewPager.adapter =
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
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
//            }
//        })
    }

}