package com.lollipop.debug.core.http

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lollipop.debug.core.base.BasicDatabaseHelper
import com.lollipop.debug.http.HttpState
import com.lollipop.debug.http.RequestType

class DHttpDataService(context: Context) : BasicDatabaseHelper(context, "debug_http_data.db", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db ?: return
        createTable(db, HttpTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 暂时还不需要更新
    }

    fun insert(info: DHttpInfo): Long {
        val db = writableDatabase
        return db.insert(HttpTable.name, null, HttpTable.getValues(info))
    }

    fun queryLimit(minTime: Long): List<DHttpInfo>? {
        TODO()
    }

    fun queryById(id: Long): DHttpInfo? {
        TODO()
    }

    object HttpTable : Table<DHttpInfo>("DebugHttp") {

        /**
        class DHttpInfo(
        val type: RequestType,
        val state: HttpState,
        val url: String,
        val time: Long,
        val header: Map<String, String>,
        val data: String,
        val response: String,
        )
         */

        private val columnId = Column.L("id", true)
        private val columnMethod = Column.S("method")
        private val columnState = Column.S("state")
        private val columnUrl = Column.S("url")
        private val columnTime = Column.L("time")
        private val columnHeader = Column.S("header")
        private val columnRequestBody = Column.S("request_body")
        private val columnResponseBody = Column.S("response_body")

        override val columns: Array<Column> by lazy {
            arrayOf(
                columnId,
                columnMethod,
                columnState,
                columnUrl,
                columnTime,
                columnHeader,
                columnRequestBody,
                columnResponseBody,
            )
        }

        fun getValues(info: DHttpInfo): ContentValues {
            val contentValues = ContentValues()
            if (info.id != DHttpInfo.NO_ID) {
                contentValues.put(columnId.name, info.id)
            }
            contentValues.put(columnMethod.name, info.type.type)
            contentValues.put(columnState.name, info.state.toJson().toString())
            contentValues.put(columnUrl.name, info.url)
            contentValues.put(columnTime.name, info.time)
            contentValues.put(columnHeader.name, DHttpInfo.formatHeader(info.header))
            contentValues.put(columnRequestBody.name, info.data)
            contentValues.put(columnResponseBody.name, info.response)
            return contentValues
        }

        override fun mapInfo(cursor: Cursor): DHttpInfo {
            return DHttpInfo(
                id = columnId.getValue(cursor) ?: DHttpInfo.NO_ID,
                type = RequestType.parse(columnMethod.getValue(cursor) ?: ""),
                state = HttpState.parse(columnState.getValue(cursor) ?: ""),
                url = columnUrl.getValue(cursor) ?: "",
                time = columnTime.getValue(cursor) ?: 0L,
                header = DHttpInfo.parseHeader(columnHeader.getValue(cursor) ?: ""),
                data = columnRequestBody.getValue(cursor) ?: "",
                response = columnResponseBody.getValue(cursor) ?: "",
            )
        }

    }

}