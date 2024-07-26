package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugItemStaticButtonBinding
import com.lollipop.debug.local.databinding.DebugItemStaticGroupBinding
import com.lollipop.debug.local.databinding.DebugItemStaticTextBinding
import com.lollipop.debug.local.databinding.DebugPanelPageStaticBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelButtonItemInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelGroupInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelItemInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelPageInfo
import com.lollipop.debug.panel.staticpanel.DebugStaticPanelTextItemInfo
import com.lollipop.debug.panel.staticpanel.GroupStateHelper

class DebugStaticPagerHolder(
    val binding: DebugPanelPageStaticBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener,
    DebugStaticPanelPageInfo.OnPageChangedListener {

    companion object {
        private fun childBuild(
            layoutInflater: LayoutInflater,
            contentGroup: LinearLayout,
            childInfoList: List<DebugStaticPanelItemInfo>,
            groupStateHelper: GroupStateHelper
        ) {
            for (index in childInfoList.indices) {
                when (val item = childInfoList[index]) {
                    is DebugStaticPanelGroupInfo -> {
                        groupBuild(
                            layoutInflater,
                            contentGroup,
                            item,
                            groupStateHelper.child(item.id, index)
                        )
                    }

                    is DebugStaticPanelButtonItemInfo -> {
                        buttonBuild(layoutInflater, contentGroup, item)
                    }

                    is DebugStaticPanelTextItemInfo -> {
                        textBuild(layoutInflater, contentGroup, item)
                    }
                }
            }
        }

        private fun groupBuild(
            layoutInflater: LayoutInflater,
            contentGroup: LinearLayout,
            info: DebugStaticPanelGroupInfo,
            groupStateHelper: GroupStateHelper
        ) {

            val binding = DebugItemStaticGroupBinding.inflate(layoutInflater, contentGroup, true)
            binding.groupLabelView.text = info.name
            binding.childGroup.isVisible = groupStateHelper.isOpen
            binding.groupExpandButton.setOnClickListener {
                val isOpen = !binding.childGroup.isVisible
                binding.childGroup.isVisible = isOpen
                groupStateHelper.isOpen = isOpen
            }

            childBuild(layoutInflater, binding.childGroup, info.itemList, groupStateHelper)
        }

        private fun buttonBuild(
            layoutInflater: LayoutInflater,
            contentGroup: LinearLayout,
            info: DebugStaticPanelButtonItemInfo
        ) {
            val binding = DebugItemStaticButtonBinding.inflate(layoutInflater, contentGroup, true)
            binding.buttonView.text = info.name
            binding.buttonView.setOnClickListener {
                info.onClickListener()
            }
        }

        private fun textBuild(
            layoutInflater: LayoutInflater,
            contentGroup: LinearLayout,
            info: DebugStaticPanelTextItemInfo
        ) {
            val binding = DebugItemStaticTextBinding.inflate(layoutInflater, contentGroup, true)
            binding.textLabelView.text = info.name
            binding.textValueView.text = info.value
        }
    }

    init {
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
    }

    private val postRefreshTask = Runnable {
        onRefresh()
    }

    private val contentGroup: LinearLayout
        get() {
            return binding.itemGroup
        }

    private var currentPanelInfo: DebugStaticPanelPageInfo? = null

    private var currentMode = -1

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        binding.refreshLayout.isRefreshing = false
        currentPanelInfo?.let {
            buildContent(it)
        }
    }

    fun bind(info: DebugPanelPageDescriptor.RemoteStatic) {
        unbindListener()
        val pageInfo = info.info
        if (pageInfo !== currentPanelInfo) {
            contentGroup.removeAllViews()
        }
        currentPanelInfo = pageInfo
        unbindListener()
        buildContent(pageInfo)
        bindListener()
    }

    private fun bindListener() {
        currentPanelInfo?.let {
            it.onPageChangedListener = this
        }
    }

    private fun unbindListener() {
        currentPanelInfo?.let {
            if (it.onPageChangedListener === this) {
                it.onPageChangedListener = null
            }
        }
    }

    override fun onPageChanged() {
        binding.root.removeCallbacks(postRefreshTask)
        binding.root.postDelayed(postRefreshTask, 500L)
    }

    private fun buildContent(info: DebugStaticPanelPageInfo) {
        if (currentMode == info.changeMode) {
            // 没有变化不要反复刷新
            return
        }
        contentGroup.removeAllViews()
        val groupStateHelper = GroupStateHelper.panel(info.id)
        val layoutInflater = LayoutInflater.from(binding.root.context)
        childBuild(layoutInflater, contentGroup, info.itemList, groupStateHelper)
        currentMode = info.changeMode
    }

    override fun onAttached() {
        bindListener()
    }

    override fun onDetached() {
        unbindListener()
    }

}