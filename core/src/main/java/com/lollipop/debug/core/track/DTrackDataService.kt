package com.lollipop.debug.core.track

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lollipop.debug.core.base.BasicDatabaseHelper
import com.lollipop.debug.core.base.ListResult
import com.lollipop.debug.core.base.StaticResult
import com.lollipop.debug.track.TrackAction

class DTrackDataService(context: Context) : BasicDatabaseHelper(context, "debug_track_data.db", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db ?: return
        createTable(db, TrackTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 暂时还不需要更新
    }

    fun insert(info: DTrackInfo): StaticResult<Unit> {
        try {
            val db = writableDatabase
            val resultCode = db.insert(TrackTable.name, null, TrackTable.getValues(info))
            if (resultCode < 0) {
                return StaticResult.Error(IllegalStateException("insert info Error: $info"))
            }
            return StaticResult.Success(Unit)
        } catch (e: Throwable) {
            return StaticResult.Error(e)
        }
    }

    fun queryLimit(minTime: Long, pageSize: Int, pageIndex: Int): ListResult<DTrackInfo> {
        return queryBuilder(TrackTable)
            .selectAll()
            .where(SqlWhere.And(TrackTable.columnTime, ">=", minTime.toString()))
            .orderBy(SqlOrder.Desc(TrackTable.columnTime))
            .pageOf(pageSize, pageIndex)
            .queryBySelectList()
    }

    fun queryById(id: Long): StaticResult<DTrackInfo> {
        return queryBuilder(TrackTable)
            .selectAll()
            .where(SqlWhere.And(TrackTable.columnId, "=", id.toString()))
            .orderBy(SqlOrder.Desc(TrackTable.columnTime))
            .queryBySelectFirst()
    }

    object TrackTable : Table<DTrackInfo>("DebugTrack") {

        /**
        class DTrackInfo(
        val action: TrackAction,
        val pageName: String,
        val targetName: String,
        val sourcePage: String,
        val message: String,
        val data: Map<String, String>,
        val extra: String,
        val time: Long
        )
         */

        val columnId = Column.L("id", true)
        val columnAction = Column.S("action")
        val columnPage = Column.S("pageName")
        val columnTarget = Column.S("targetName")
        val columnSource = Column.S("sourcePage")
        val columnMessage = Column.S("message")
        val columnData = Column.S("data")
        val columnExtra = Column.S("extra")
        val columnTime = Column.L("time")

        override val columns: Array<Column> by lazy {
            arrayOf(
                columnId,
                columnAction,
                columnPage,
                columnTarget,
                columnSource,
                columnMessage,
                columnData,
                columnExtra,
                columnTime
            )
        }

        fun getValues(info: DTrackInfo): ContentValues {
            val contentValues = ContentValues()
            if (info.id != DTrackInfo.NO_ID) {
                contentValues.put(columnId.name, info.id)
            }
            columnAction.putValue(contentValues, info.action.type)
            columnPage.putValue(contentValues, info.pageName)
            columnTarget.putValue(contentValues, info.targetName)
            columnSource.putValue(contentValues, info.sourcePage)
            columnMessage.putValue(contentValues, info.message)
            columnData.putValue(contentValues, info.dataToJson())
            columnExtra.putValue(contentValues, info.extra)
            columnTime.putValue(contentValues, info.time)
            return contentValues
        }

        override fun mapInfo(cursor: Cursor): DTrackInfo {
            return DTrackInfo(
                id = columnId.getValue(cursor) ?: DTrackInfo.NO_ID,
                action = TrackAction.parse(columnAction.getValue(cursor) ?: ""),
                pageName = columnPage.getValue(cursor) ?: "",
                targetName = columnTarget.getValue(cursor) ?: "",
                sourcePage = columnSource.getValue(cursor) ?: "",
                message = columnMessage.getValue(cursor) ?: "",
                data = columnData.getValue(cursor)?.let { DTrackInfo.parseJson(it) } ?: emptyMap(),
                extra = columnExtra.getValue(cursor) ?: "",
                time = columnTime.getValue(cursor) ?: 0L
            )
        }

    }

}