package com.customerio.shared.database

import com.customerio.shared.CioDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CioDatabase.Schema, "customerio.db")
    }
}