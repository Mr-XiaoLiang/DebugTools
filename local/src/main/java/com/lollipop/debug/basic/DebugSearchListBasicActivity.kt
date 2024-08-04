package com.lollipop.debug.basic

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.debug.helper.RecyclerViewLoadMoreHelper
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.ActivityDebugSearchListBasicBinding
import java.util.concurrent.Executors

abstract class DebugSearchListBasicActivity<T> : AppCompatActivity(),
    SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun start(context: Context, clazz: Class<out DebugSearchListBasicActivity<*>>) {
            context.startActivity(
                Intent(context, clazz).apply {
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            )
        }
    }

    protected val binding by lazy {
        ActivityDebugSearchListBasicBinding.inflate(layoutInflater)
    }

    protected var searchInfo = ""

    protected val displayToastList = ArrayList<T>()

    protected val executor = Executors.newCachedThreadPool()

    protected val backPressedWithSearch = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            cleanSearch()
        }
    }

    protected val searchPendingTask = Runnable {
        searchInfo()
    }

    protected val taskHandler = Handler(Looper.getMainLooper())

    protected var adapter: RecyclerView.Adapter<*>? = null

    protected open val pageSize = 20

    protected var pageIndex = 0

    protected abstract val loadMoreEnable: Boolean

    protected val loadMoreHelper = RecyclerViewLoadMoreHelper(
        loadMoreStateProvider = {
            canLoadMore()
        },
        loadMoreListener = {
            onLoadMore()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        initSearchBar()
        initContentView()
    }

    private fun initSearchBar() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(backPressedWithSearch)
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        binding.searchInputView.setOnFocusChangeListener { _, _ ->
            updateSearchBarIcon()
        }
        binding.searchInputView.doOnTextChanged { _, _, _, _ ->
            updateSearchBarIcon()
            onSearchInfoChanged()
        }
        binding.cleanSearchButton.setOnClickListener {
            cleanSearch()
        }
        binding.requestSearchButton.isFocusable = false
    }

    private fun initContentView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentListView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(
            R.color.debugRefreshLayoutColorScheme1,
            R.color.debugRefreshLayoutColorScheme2,
            R.color.debugRefreshLayoutColorScheme3,
            R.color.debugRefreshLayoutColorScheme4
        )
        adapter = createAdapter(displayToastList)
        binding.contentListView.adapter = adapter
        binding.contentListView.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        loadMoreHelper.attach(binding.contentListView)
    }

    protected abstract fun createAdapter(dataList: List<T>): RecyclerView.Adapter<*>

    private fun hideKeyboard(view: View) {
        val windowController = WindowCompat.getInsetsController(window, view)
        windowController.hide(WindowInsetsCompat.Type.ime())
//        return
//        val imm = ViewUtils.getInputMethodManager(view)
//        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("SetTextI18n")
    private fun cleanSearch() {
        binding.searchInputView.setText("")
        binding.searchInputView.clearFocus()
        hideKeyboard(binding.searchInputView)
    }

    private fun updateSearchBarIcon() {
        val hasFocus = binding.searchInputView.hasFocus()
        val info = binding.searchInputView.text ?: ""
        val needClean = hasFocus || info.isNotEmpty()
        binding.cleanSearchButton.isVisible = needClean
        binding.requestSearchButton.isVisible = !needClean
        backPressedWithSearch.isEnabled = needClean
    }

    private fun onSearchInfoChanged() {
        searchInfo = binding.searchInputView.text?.toString() ?: ""
        onRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchInfo() {
        val index = pageIndex
        getSearchResult(searchInfo, pageSize, index) { result ->
            taskHandler.post {
                if (index < 1) {
                    displayToastList.clear()
                }
                val start = displayToastList.size
                displayToastList.addAll(result)
                if (start == 0) {
                    adapter?.notifyDataSetChanged()
                } else {
                    adapter?.notifyItemRangeInserted(start, result.size)
                }
                if (index < 1) {
                    binding.refreshLayout.isRefreshing = false
                } else {
                    loadMoreHelper.isLoading = false
                }
            }
        }
    }

    protected open fun getSearchResult(
        key: String,
        pageSize: Int,
        pageIndex: Int,
        callback: (List<T>) -> Unit
    ) {
        executor.execute {
            getSearchResultAsync(key, pageSize, pageIndex, callback)
        }
    }

    protected abstract fun getSearchResultAsync(
        key: String,
        pageSize: Int,
        pageIndex: Int,
        callback: (List<T>) -> Unit
    )

    override fun onRefresh() {
        pageIndex = 0
        binding.refreshLayout.isRefreshing = true
        taskHandler.removeCallbacks(searchPendingTask)
        taskHandler.postDelayed(searchPendingTask, 300)
    }

    protected open fun onLoadMore() {
        if (binding.refreshLayout.isRefreshing) {
            return
        }
        pageIndex++
        taskHandler.removeCallbacks(searchPendingTask)
        taskHandler.post(searchPendingTask)
    }

    protected open fun canLoadMore(): Boolean {
        return loadMoreEnable && !binding.refreshLayout.isRefreshing
    }

    override fun onResume() {
        super.onResume()
        if (displayToastList.isEmpty()) {
            onRefresh()
        }
    }

}