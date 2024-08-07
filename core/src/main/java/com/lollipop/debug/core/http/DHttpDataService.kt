package com.lollipop.debug.core.http

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lollipop.debug.core.base.BasicDatabaseHelper
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.base.StaticResult
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

    fun insert(info: DHttpInfo): StaticResult<Unit> {
        try {
            val db = writableDatabase
            val resultCode = db.insert(HttpTable.name, null, HttpTable.getValues(info))
            if (resultCode < 0) {
                return StaticResult.Error(IllegalStateException("insert info Error: $info"))
            }
            return StaticResult.Success(Unit)
        } catch (e: Throwable) {
            return StaticResult.Error(e)
        }
    }

    fun queryLimit(minTime: Long, pageSize: Int, pageIndex: Int): ListResult<DHttpInfo> {
        return queryBuilder(HttpTable)
            .selectAll()
            .where(SqlWhere.And(HttpTable.columnTime, ">=", minTime.toString()))
            .orderBy(SqlOrder.Desc(HttpTable.columnTime))
            .pageOf(pageSize, pageIndex)
            .queryBySelectList()
    }

    fun queryById(id: Long): StaticResult<DHttpInfo> {
        return queryBuilder(HttpTable)
            .selectAll()
            .where(SqlWhere.And(HttpTable.columnId, "=", id.toString()))
            .orderBy(SqlOrder.Desc(HttpTable.columnTime))
            .queryBySelectFirst()
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

        val columnId = Column.L("id", true)
        val columnMethod = Column.S("method")
        val columnState = Column.S("state")
        val columnUrl = Column.S("url")
        val columnTime = Column.L("time")
        val columnHeader = Column.S("header")
        val columnRequestBody = Column.S("request_body")
        val columnResponseBody = Column.S("response_body")

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
            columnMethod.putValue(contentValues, info.type.type)
            columnState.putValue(contentValues, info.state.toJson().toString())
            columnUrl.putValue(contentValues, info.url)
            columnTime.putValue(contentValues, info.time)
            columnHeader.putValue(contentValues, DHttpInfo.formatHeader(info.header))
            columnRequestBody.putValue(contentValues, info.data)
            columnResponseBody.putValue(contentValues, info.response)
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