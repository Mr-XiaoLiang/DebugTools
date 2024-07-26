package com.lollipop.debug.panel.pager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugItemStaticButtonBinding
import com.lollipop.debug.local.databinding.DebugPanelPageHomeBinding
import com.lollipop.debug.panel.DebugPanelImpl

class DebugHomePagerHolder(
    val binding: DebugPanelPageHomeBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener {

    private val currentMenuData = ArrayList<MenuInfo>()
    private val adapter = MenuAdapter(currentMenuData, ::onItemClick)

    init {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(
            binding.root.context, RecyclerView.VERTICAL, false
        )
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
        onRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        syncData()
        adapter.notifyDataSetChanged()
        binding.refreshLayout.isRefreshing = false
    }

    private fun onItemClick(info: MenuInfo) {
        DebugPanelImpl.navigate(info.id)
    }

    private fun syncData() {
        val infos = DebugPanelImpl.getAllPages().map { MenuInfo(id = it.id, name = it.name) }
        currentMenuData.clear()
        currentMenuData.addAll(infos)
    }

    private class MenuInfo(
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