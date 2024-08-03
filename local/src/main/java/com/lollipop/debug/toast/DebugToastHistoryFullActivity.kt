package com.lollipop.debug.toast

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
import com.lollipop.debug.DebugToastHelper
import com.lollipop.debug.local.R
import com.lollipop.debug.local.databinding.ActivityDebugToastHistoryFullBinding
import java.util.concurrent.Executors

class DebugToastHistoryFullActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(context, DebugToastHistoryFullActivity::class.java).apply {
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            )
        }
    }

    private val binding by lazy {
        ActivityDebugToastHistoryFullBinding.inflate(layoutInflater)
    }

    private var searchInfo = ""

    private val displayToastList = ArrayList<ToastInfo>()

    private val executor = Executors.newCachedThreadPool()

    private val backPressedWithSearch = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            cleanSearch()
        }
    }

    private val searchPendingTask = Runnable {
        searchInfo()
    }

    private val taskHandler = Handler(Looper.getMainLooper())

    private val adapter = ToastHistoryAdapter(displayToastList)

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
        ViewCompat.setOnApplyWindowInsetsListener(binding.toastListView) { v, insets ->
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
        binding.toastListView.adapter = adapter
        binding.toastListView.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
    }

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
        getSearchResult(searchInfo) {
            taskHandler.post {
                displayToastList.clear()
                displayToastList.addAll(it)
                adapter.notifyDataSetChanged()
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun getSearchResult(key: String, callback: (List<ToastInfo>) -> Unit) {
        executor.execute {
            val allList = ArrayList<ToastInfo>()
            allList.addAll(DebugToastHelper.toastHistory)
            if (key.isEmpty()) {
                callback(allList)
                return@execute
            }
            val result = ArrayList<ToastInfo>()
            allList.forEach {
                if (it.text.contains(key) || it.detail.contains(key)) {
                    result.add(it)
                }
            }
            callback(result)
        }
    }

    override fun onRefresh() {
        binding.refreshLayout.isRefreshing = true
        taskHandler.removeCallbacks(searchPendingTask)
        taskHandler.postDelayed(searchPendingTask, 300)
    }

    override fun onResume() {
        super.onResume()
        if (displayToastList.isEmpty()) {
            onRefresh()
        }
    }

}