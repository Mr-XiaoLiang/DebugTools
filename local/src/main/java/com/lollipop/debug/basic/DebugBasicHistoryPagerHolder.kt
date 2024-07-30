package com.lollipop.debug.basic

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.helper.RecyclerViewLoadMoreHelper
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.DebugPanelPageBasicHistoryBinding
import com.lollipop.debug.panel.DebugPanelPageDescriptor

abstract class DebugBasicHistoryPagerHolder<T>(
    val binding: DebugPanelPageBasicHistoryBinding
) : DebugPagerHolder(binding.root), SwipeRefreshLayout.OnRefreshListener {

    protected var adapter: RecyclerView.Adapter<*>? = null
    protected var isAttached = false
    private var isInitialized = false

    private val loadMoreHelper = RecyclerViewLoadMoreHelper(
        loadMoreStateProvider = {
            canLoadMore()
        },
        loadMoreListener = {
            callLoadMore()
        },
    )

    protected fun initPager() {
        if (isInitialized) {
            return
        }
        if (binding.recyclerView.adapter == null) {
            val ada = adapter ?: createAdapter()
            adapter = ada
            binding.recyclerView.adapter = ada
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager = LinearLayoutManager(
                binding.root.context, RecyclerView.VERTICAL, false
            )
        }
        loadMoreHelper.attach(binding.recyclerView)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
        notifyOptionsChanged()
        isInitialized = true
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>

    override fun onBind(info: DebugPanelPageDescriptor) {
        initPager()
        onRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    protected fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    protected abstract fun createOptions(): List<Option>

    protected fun notifyOptionsChanged() {
        val optionList = createOptions()
        if (optionList.isEmpty()) {
            binding.bottomBar.isVisible = false
            return
        }
        binding.bottomBar.isVisible = true
        binding.bottomBar.removeAllViews()
        for (option in optionList) {
            binding.bottomBar.addView(createOptionButton(option))
        }
    }

    protected fun notifyItemRangeInserted(start: Int, count: Int) {
        adapter?.notifyItemRangeInserted(start, count)
    }

    protected fun callRefresh() {
        startRefresh()
        onRefresh()
    }

    protected fun startRefresh() {
        binding.refreshLayout.isRefreshing = true
    }

    protected fun onRefreshEnd() {
        binding.refreshLayout.isRefreshing = false
    }

    protected fun onLoadMoreEnd() {
        loadMoreHelper.isLoading = false
    }

    protected open fun canLoadMore(): Boolean {
        return isAttached && !binding.refreshLayout.isRefreshing
    }

    protected abstract fun callLoadMore()

    override fun onAttached() {
        isAttached = true
    }

    override fun onDetached() {
        isAttached = false

    }

    private fun createOptionButton(option: Option): View {
        val context = itemView.context
        val optionColor = getOptionColor(option)
        val button = LinearLayout(context)
        button.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            weight = option.weight
        }
        button.gravity = Gravity.CENTER
        button.orientation = LinearLayout.HORIZONTAL
        if (option.hasIcon) {
            val buttonIcon = AppCompatImageView(context)
            buttonIcon.setImageResource(option.icon)
            val dp24 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24F,
                context.resources.displayMetrics
            ).toInt()
            buttonIcon.imageTintList = ColorStateList.valueOf(optionColor)
            button.addView(buttonIcon, dp24, dp24)
        }

        if (option.hasTitle) {
            val buttonText = AppCompatTextView(context)
            when (option) {
                is Option.Resource -> {
                    buttonText.setText(option.titleId)
                }

                is Option.Static -> {
                    buttonText.text = option.title
                }
            }
            buttonText.setTextColor(optionColor)
            button.addView(
                buttonText,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        button.setOnClickListener {
            option.onClick()
        }
        return button
    }

    protected open fun getOptionColor(option: Option): Int {
        return 0xFF005064.toInt()
    }

    protected fun option(
        title: String,
        icon: Int = 0,
        weight: Float = 1F,
        onClick: () -> Unit
    ): Option {
        return Option.Static(title, icon, weight, onClick)
    }

    protected fun option(
        title: Int,
        icon: Int = 0,
        weight: Float = 1F,
        onClick: () -> Unit
    ): Option {
        return Option.Resource(title, icon, weight, onClick)
    }

    sealed class Option(
        val icon: Int,
        val weight: Float,
        val onClick: () -> Unit
    ) {

        val hasIcon: Boolean = icon != 0
        abstract val hasTitle: Boolean

        class Static(
            val title: String,
            icon: Int = 0,
            weight: Float = 1F,
            onClick: () -> Unit
        ) : Option(icon, weight, onClick) {
            override val hasTitle: Boolean = title.isNotEmpty()
        }

        class Resource(
            val titleId: Int,
            icon: Int = 0,
            weight: Float = 1F,
            onClick: () -> Unit
        ) : Option(icon, weight, onClick) {
            override val hasTitle: Boolean = titleId != 0
        }

    }

}