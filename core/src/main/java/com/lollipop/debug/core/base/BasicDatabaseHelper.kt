package com.lollipop.debug.core.base

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

abstract class BasicDatabaseHelper(
    context: Context,
    name: String,
    version: Int,
) : SQLiteOpenHelper(context, name, null, version) {

    protected fun createTable(db: SQLiteDatabase, table: Table<*>) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS ")
            .append(table.name)
            .append(" (")
        for (index in table.columns.indices) {
            val column = table.columns[index]
            if (index > 0) {
                sql.append(",")
            }
            sql.append(column.name)
                .append(" ")
                .append(
                    when (column.type) {
                        ColumnType.Int -> "INTEGER"
                        ColumnType.Long -> "INTEGER"
                        ColumnType.Double -> "REAL"
                        ColumnType.String -> "TEXT"
                    }
                )
            if (column.isPrimaryKey) {
                sql.append(" PRIMARY KEY")
            }
        }
        sql.append(")")
        db.execSQL(sql.toString())
    }

    protected fun <T : Any> select(
        db: SQLiteDatabase = readableDatabase,
        table: Table<T>,
        queryBuilder: QuerySqlBuilder,
    ): List<T> {
        val resultList = ArrayList<T>()
        val cursor = db.rawQuery(queryBuilder.buildQuerySql(), queryBuilder.buildQueryArguments())
        while (cursor.moveToNext()) {
            resultList.add(table.mapInfo(cursor))
        }
        cursor.close();
        return resultList;

    }

    protected class QuerySqlBuilder(
        val sqlType: SqlType
    ) {
        private val columns = mutableListOf<Column>()
        private var targetTable: Table<*>? = null
        private val where = mutableListOf<SqlWhere>()
        private var limitOffset = 0
        private var limitCount = 0

        fun buildQuerySql(): String {
            return when (sqlType) {
                SqlType.Select -> queryBySelect()
                SqlType.Insert -> TODO()
                SqlType.Update -> TODO()
                SqlType.Delete -> TODO()
            }
        }

        fun buildQueryArguments(): Array<String> {
            return when (sqlType) {
                SqlType.Select -> buildQueryArguments()
                SqlType.Insert -> TODO()
                SqlType.Update -> TODO()
                SqlType.Delete -> TODO()
            }
        }

        fun select(vararg c: Column): QuerySqlBuilder {
            columns.addAll(c)
            return this
        }

        fun limit(offset: Int, count: Int): QuerySqlBuilder {
            limitOffset = offset
            limitCount = count
            return this
        }

        fun pageOf(pageSize: Int, pageIndex: Int): QuerySqlBuilder {
            limitOffset = pageIndex * pageSize
            limitCount = pageSize
            return this
        }

        fun selectAll(): QuerySqlBuilder {
            columns.addAll(targetTable!!.columns)
            return this
        }

        fun where(vararg w: SqlWhere): QuerySqlBuilder {
            where.addAll(w)
            return this
        }

        private fun argumentsBySelect(): Array<String> {
            return where.map { it.value }.toTypedArray()
        }

        private fun queryBySelect(): String {
            val table = targetTable ?: throw IllegalStateException("table is null")
            val sql = StringBuilder()
            sql.append("SELECT ")
            for (index in columns.indices) {
                if (index > 0) {
                    sql.append(",")
                }
                sql.append(columns[index].name)
            }
            sql.append(" FROM ").append(table.name).append(" ")
            if (where.isNotEmpty()) {
                sql.append(" WHERE ")
                for (index in where.indices) {
                    val whereItem = where[index]
                    if (index > 0) {
                        when (whereItem) {
                            is SqlWhere.And -> {
                                sql.append(" AND ")
                            }

                            is SqlWhere.Or -> {
                                sql.append(" OR ")
                            }
                        }
                    }
                    sql.append(whereItem.column.name)
                        .append(" ")
                        .append(whereItem.option)
                        .append(" ?")
                }
            }
            if (limitCount > 0) {
                sql.append(" LIMIT ")
                    .append(limitOffset)
                    .append(",")
                    .append(limitCount)
            }
            return sql.toString()
        }

        fun from(table: Table<*>): QuerySqlBuilder {
            this.targetTable = table
            return this
        }
    }

    protected sealed class SqlWhere(
        val column: Column,
        val value: String,
        val option: String
    ) {
        class And(column: Column, option: String, value: String) : SqlWhere(column, value, option)
        class Or(column: Column, option: String, value: String) : SqlWhere(column, value, option)
    }

    protected enum class SqlType {
        Select,
        Insert,
        Update,
        Delete,
    }

    enum class ColumnType {
        Int,
        Long,
        Double,
        String,
    }

    abstract class Table<T : Any>(
        val name: String
    ) {
        val columns = mutableListOf<Column>()

        abstract fun mapInfo(cursor: Cursor): T

    }

    class Column(
        val name: String,
        val type: ColumnType,
        val isPrimaryKey: Boolean = false,
    ) {

        fun getValue(cursor: Cursor): Any? {
            return when (type) {
                ColumnType.Int -> cursor.getIntOrNull(cursor.getColumnIndex(name))
                ColumnType.Long -> cursor.getLongOrNull(cursor.getColumnIndex(name))
                ColumnType.Double -> cursor.getDoubleOrNull(cursor.getColumnIndex(name))
                ColumnType.String -> cursor.getStringOrNull(cursor.getColumnIndex(name))
            }
        }
    }

}