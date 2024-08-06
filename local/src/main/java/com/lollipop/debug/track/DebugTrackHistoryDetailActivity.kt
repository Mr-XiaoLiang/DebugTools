package com.lollipop.debug.track

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.debug.core.track.DTrackInfo
import com.lollipop.debug.local.databinding.ActivityDebugTrackHistoryDetailBinding
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryBinding
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryExtraBinding
import com.lollipop.debug.local.databinding.DebugItemTrackHistoryParamsBinding
import org.json.JSONObject

class DebugTrackHistoryDetailActivity : AppCompatActivity() {

    companion object {

        private const val PARAMS_TRACK_INFO = "track_info"

        private const val KEY_ID = "id"
        private const val KEY_ACTION = "action"
        private const val KEY_PAGE_NAME = "page_name"
        private const val KEY_TARGET_NAME = "target_name"
        private const val KEY_SOURCE_PAGE = "source_page"
        private const val KEY_MESSAGE = "message"
        private const val KEY_DATA = "data"
        private const val KEY_EXTRA = "extra"
        private const val KEY_TIME = "time"

        fun start(context: Context, detail: DTrackInfo) {
            context.startActivity(
                Intent(
                    context,
                    DebugTrackHistoryDetailActivity::class.java
                ).apply {
                    putExtra(PARAMS_TRACK_INFO, trackToJson(detail))
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                })
        }

        private fun trackToJson(info: DTrackInfo): String {
            val jsonObject = JSONObject()
            //    val id: Long = NO_ID,
            //    val action: TrackAction,
            //    val pageName: String,
            //    val targetName: String,
            //    val sourcePage: String,
            //    val message: String,
            //    val data: Map<String, String>,
            //    val extra: String,
            //    val time: Long
            jsonObject.apply {
                put(KEY_ID, info.id)
                put(KEY_ACTION, info.action.type)
                put(KEY_PAGE_NAME, info.pageName)
                put(KEY_TARGET_NAME, info.targetName)
                put(KEY_SOURCE_PAGE, info.sourcePage)
                put(KEY_MESSAGE, info.message)
                put(KEY_DATA, info.dataToJsonObj())
                put(KEY_EXTRA, info.extra)
                put(KEY_TIME, info.time)
            }
            return jsonObject.toString()
        }

        private fun jsonToTrack(json: String): DTrackInfo? {
            try {
                val jsonObject = JSONObject(json)
                return DTrackInfo(
                    id = jsonObject.optLong(KEY_ID, DTrackInfo.NO_ID),
                    action = TrackAction.parse(jsonObject.optString(KEY_ACTION, "")),
                    pageName = jsonObject.optString(KEY_PAGE_NAME, ""),
                    targetName = jsonObject.optString(KEY_TARGET_NAME, ""),
                    sourcePage = jsonObject.optString(KEY_SOURCE_PAGE, ""),
                    message = jsonObject.optString(KEY_MESSAGE, ""),
                    data = DTrackInfo.parseJson(jsonObject.optString(KEY_DATA, "")),
                    extra = jsonObject.optString(KEY_EXTRA, ""),
                    time = jsonObject.optLong(KEY_TIME, 0)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return null
        }

    }

    private val binding by lazy {
        ActivityDebugTrackHistoryDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trackInfo = jsonToTrack(intent.getStringExtra(PARAMS_TRACK_INFO) ?: "")
        if (trackInfo == null) {
            finish()
            return
        }
        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentListView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        val trackItems = trackToList(trackInfo)
        binding.contentListView.adapter = TrackAdapter(trackItems)
        binding.contentListView.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun trackToList(info: DTrackInfo): List<TrackItem> {
        val result = mutableListOf<TrackItem>()
        result.add(TrackItem.Header(info))
        info.data.entries.forEach { entry ->
            result.add(TrackItem.Params(entry.key, entry.value))
        }
        result.add(TrackItem.Extra(info.extra))
        return result
    }

    private class TrackAdapter(
        val data: List<TrackItem>
    ) : RecyclerView.Adapter<DTrackItemHolder>() {

        companion object {
            private const val TYPE_HEADER = 1
            private const val TYPE_PARAMS = 2
            private const val TYPE_EXTRA = 3
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DTrackItemHolder {
            when (viewType) {
                TYPE_HEADER -> {
                    return DTrackItemHolder.Header(
                        DebugItemTrackHistoryBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        ),
                        ::onHeaderClick
                    )
                }

                TYPE_PARAMS -> {
                    return DTrackItemHolder.Params(
                        DebugItemTrackHistoryParamsBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }

                TYPE_EXTRA -> {
                    return DTrackItemHolder.Extra(
                        DebugItemTrackHistoryExtraBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }

                else -> {
                    throw IllegalArgumentException("unknown viewType: $viewType")
                }

            }
        }

        private fun onHeaderClick(position: Int) {
            // nothing
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: DTrackItemHolder, position: Int) {
            val item = data[position]
            when (holder) {
                is DTrackItemHolder.Header -> {
                    if (item is TrackItem.Header) {
                        holder.bind(item.info)
                    }
                }

                is DTrackItemHolder.Params -> {
                    if (item is TrackItem.Params) {
                        holder.bind(item.key, item.value)
                    }
                }

                is DTrackItemHolder.Extra -> {
                    if (item is TrackItem.Extra) {
                        holder.bind(item.value)
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            when (val item = data[position]) {
                is TrackItem.Header -> {
                    return TYPE_HEADER
                }

                is TrackItem.Params -> {
                    return TYPE_PARAMS
                }

                is TrackItem.Extra -> {
                    return TYPE_EXTRA
                }

                else -> {
                    throw IllegalArgumentException("unknown item: $item")
                }
            }
        }

    }

    private sealed class TrackItem {
        class Header(val info: DTrackInfo) : TrackItem()
        class Params(val key: String, val value: String) : TrackItem()
        class Extra(val value: String) : TrackItem()
    }

}