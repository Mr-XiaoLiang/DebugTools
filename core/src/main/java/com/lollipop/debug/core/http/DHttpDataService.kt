package com.lollipop.debug.core.http

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lollipop.debug.core.base.BasicDatabaseHelper

class DHttpDataService(context: Context) : BasicDatabaseHelper(context, "debug_http_data.db", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    class HttpTable : BasicDatabaseHelper.Table<DHttpInfo>("DebugHttp") {

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
        private val columnArray by lazy {
            arrayOf(
                Column("id", ColumnType.Long, true),
                Column("url", ColumnType.String),
                Column("method", ColumnType.String),
                Column("time", ColumnType.Long),
                Column("state", ColumnType.Long),
                Column("cost_time", ColumnType.Long),
                Column("request_body", ColumnType.String),
                Column("response_body", ColumnType.String),
                Column("error_msg", ColumnType.String),
            )
        }

        override val columns: Array<Column>
            get() {
                return columnArray
            }


        override fun mapInfo(cursor: Cursor): DHttpInfo {
            TODO("Not yet implemented")
        }


    }

}