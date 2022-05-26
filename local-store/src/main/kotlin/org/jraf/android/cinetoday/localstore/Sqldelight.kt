package org.jraf.android.cinetoday.localstore

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver

fun createSqldelightDatabase(context: Context): Database {
    val driver = AndroidSqliteDriver(Database.Schema, context, "cinetoday3.db")
    return Database(driver)
}
