package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.basic.DebugBasicHistoryPagerHolder
import com.lollipop.debug.local.databinding.DebugItemStaticButtonBinding
import com.lollipop.debug.panel.DebugPanelImpl
import com.lollipop.debug.panel.DebugPanelPageDescriptor
import com.lollipop.debug.panel.pager.DebugHomePagerHolder.MenuInfo

class DebugHomePagerHolder(
    parent: ViewGroup
) : DebugBasicHistoryPagerHolder<MenuInfo>(parent) {

    private val currentMenuData = ArrayList<MenuInfo>()

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return MenuAdapter(currentMenuData, ::onItemClick)
    }

    override fun onBind(info: DebugPanelPageDescriptor) {
        super.onBind(info)
        if (currentMenuData.isEmpty()) {
            callRefresh()
        }
    }

    override fun createOptions(): List<Option> {
        return emptyList()
    }

    override fun canLoadMore(): Boolean {
        return false
    }

    override fun callLoadMore() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        syncData()
        notifyDataSetChanged()
        onRefreshEnd()
    }

    private fun onItemClick(info: MenuInfo) {
        DebugPanelImpl.navigate(info.id)
    }

    private fun syncData() {
        val infos = DebugPanelImpl.getAllPages().map { MenuInfo(id = it.id, name = it.name) }
        currentMenuData.clear()
        currentMenuData.addAll(infos)
    }

    class MenuInfo(
        val id: String,
        val name: String
    )

    private class MenuAdapter(
        val data: List<MenuInfo>,
        private val onItemClick: (MenuInfo) -> Unit
    ) : RecyclerView.Adapter<MenuItem>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItem {
            return MenuItem(
                DebugItemStaticButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                ::onItemClick
            )
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= itemCount) {
                return
            }
            val info = data[position]
            onItemClick(info)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: MenuItem, position: Int) {
            holder.bind(data[position])
        }

    }

    private class MenuItem(
        val binding: DebugItemStaticButtonBinding,
        val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonView.setOnClickListener {
                onClick()
            }
        }

        private fun onClick() {
            onItemClick(adapterPosition)
        }

        fun bind(info: MenuInfo) {
            binding.buttonView.text = info.name
        }

    }

    override fun onAttached() {
    }

    override fun onDetached() {
    }

}