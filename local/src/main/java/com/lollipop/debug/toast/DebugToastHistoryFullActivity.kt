package com.lollipop.debug.toast

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.lollipop.debug.local.databinding.ActivityDebugToastHistoryFullBinding

class DebugToastHistoryFullActivity : AppCompatActivity() {

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

    private val backPressedWithSearch = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            cleanSearch()
        }
    }

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
        binding.searchInputView.setOnFocusChangeListener { v, hasFocus ->
            updateSearchBarIcon()
        }
        binding.searchInputView.doOnTextChanged { text, start, before, count ->
            updateSearchBarIcon()
            onSearchInfoChanged()
        }
        binding.cleanSearchButton.setOnClickListener {
            cleanSearch()
        }
        binding.requestSearchButton.isFocusable = false
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
        // TODO()
    }

    private fun initContentView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toastListView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

}