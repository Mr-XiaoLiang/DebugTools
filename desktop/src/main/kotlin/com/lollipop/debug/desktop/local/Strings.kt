package com.lollipop.debug.desktop.local

import com.lollipop.debug.desktop.local.impl.LocalStringsCn

object Strings : LocalStrings {

    var current: LocalStrings = LocalStringsCn

    val localList: Array<LocalStrings> = arrayOf(
        LocalStringsCn
    )

    fun findLocal(id: String): LocalStrings? {
        localList.forEach { local ->
            if (local.localId == id) {
                return local
            }
        }
        return null
    }

    override val localName: String
        get() {
            return current.localName
        }
    override val localId: String
        get() {
            return current.localId
        }
    override val appName: String
        get() {
            return current.appName
        }

}

interface LocalStrings {

    val localName: String

    val localId: String

    val appName: String

}
