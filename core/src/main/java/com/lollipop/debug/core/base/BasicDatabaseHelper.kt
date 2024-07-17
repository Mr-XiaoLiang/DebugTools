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

    protected fun <T : Any> selectList(
        db: SQLiteDatabase = readableDatabase,
        queryBuilder: QuerySqlBuilder<T>,
    ): List<T> {
        val resultList = ArrayList<T>()
        val cursor = db.rawQuery(queryBuilder.buildQuerySql(), queryBuilder.buildQueryArguments())
        while (cursor.moveToNext()) {
            resultList.add(queryBuilder.targetTable.mapInfo(cursor))
        }
        cursor.close();
        return resultList;
    }

    protected fun <T : Any> queryBuilder(table: Table<T>): QuerySqlBuilder<T> {
        return QuerySqlBuilder(this, table)
    }

    protected class QuerySqlBuilder<T : Any>(
        val dbHelper: BasicDatabaseHelper,
        val targetTable: Table<T>,
    ) {

        private val columns = mutableListOf<Column>()
        private val where = mutableListOf<SqlWhere>()
        private var limitOffset = 0
        private var limitCount = 0
        private var orderBy = mutableListOf<SqlOrder>()

        fun buildQuerySql(): String {
            return buildQuerySqlBySelect()
        }

        fun buildQueryArguments(): Array<String> {
            return buildArgumentsBySelect()
        }

        fun select(vararg c: Column): QuerySqlBuilder<T> {
            columns.addAll(c)
            return this
        }

        fun orderBy(vararg values: SqlOrder): QuerySqlBuilder<T> {
            orderBy.addAll(values)
            return this
        }

        fun limit(offset: Int, count: Int): QuerySqlBuilder<T> {
            limitOffset = offset
            limitCount = count
            return this
        }

        fun pageOf(pageSize: Int, pageIndex: Int): QuerySqlBuilder<T> {
            limitOffset = pageIndex * pageSize
            limitCount = pageSize
            return this
        }

        fun selectAll(): QuerySqlBuilder<T> {
            columns.addAll(targetTable.columns)
            return this
        }

        fun where(vararg w: SqlWhere): QuerySqlBuilder<T> {
            where.addAll(w)
            return this
        }

        private fun buildArgumentsBySelect(): Array<String> {
            return where.map { it.value }.toTypedArray()
        }

        private fun buildQuerySqlBySelect(): String {
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
            if (orderBy.isNotEmpty()) {
                sql.append(" ORDER BY ")
                for (index in orderBy.indices) {
                    if (index > 0) {
                        sql.append(",")
                    }
                    val order = orderBy[index]
                    when (order) {
                        is SqlOrder.Asc -> {
                            sql.append(order.column.name).append(" ASC ")
                        }

                        is SqlOrder.Desc -> {
                            sql.append(order.column.name).append(" DESC ")
                        }
                    }
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

        fun queryBySelectList(): List<T> {
            return dbHelper.selectList(dbHelper.readableDatabase, this)
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

    protected sealed class SqlOrder(
        val column: Column
    ) {
        class Asc(column: Column) : SqlOrder(column)
        class Desc(column: Column) : SqlOrder(column)
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
        abstract val columns: Array<Column>

        abstract fun mapInfo(cursor: Cursor): T

    }

    sealed class Column(
        val name: String,
        val type: ColumnType,
        val isPrimaryKey: Boolean = false,
    ) {

        protected fun <T> getIndex(
            cursor: Cursor,
            block: (index: Int) -> T?
        ): T? {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return null
            }
            return block(columnIndex)
        }

        class I(
            name: String,
            isPrimaryKey: Boolean = false
        ) : Column(name, ColumnType.Int, isPrimaryKey) {
            fun getValue(cursor: Cursor): Int? {
                return getIndex(cursor) { cursor.getIntOrNull(it) }
            }
        }

        class L(
            name: String,
            isPrimaryKey: Boolean = false
        ) : Column(name, ColumnType.Long, isPrimaryKey) {
            fun getValue(cursor: Cursor): Long? {
                return getIndex(cursor) { cursor.getLongOrNull(it) }
            }
        }

        class D(
            name: String,
            isPrimaryKey: Boolean = false
        ) : Column(name, ColumnType.Double, isPrimaryKey) {
            fun getValue(cursor: Cursor): Double? {
                return getIndex(cursor) { cursor.getDoubleOrNull(it) }
            }
        }

        class S(
            name: String,
            isPrimaryKey: Boolean = false
        ) : Column(name, ColumnType.String, isPrimaryKey) {
            fun getValue(cursor: Cursor): String? {
                return getIndex(cursor) { cursor.getStringOrNull(it) }
            }
        }

    }

}