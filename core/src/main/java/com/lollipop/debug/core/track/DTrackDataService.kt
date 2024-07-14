package com.lollipop.debug.core.track

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.lollipop.debug.core.base.BasicDatabaseHelper

class DTrackDataService(context: Context): BasicDatabaseHelper(context, "debug_track_data.db", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}