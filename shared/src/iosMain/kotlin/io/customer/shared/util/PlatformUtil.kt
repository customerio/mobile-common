package io.customer.shared.util

import platform.Foundation.NSUUID

internal actual fun getDatabaseUtil(): DatabaseUtil = IOSDatabaseUtil()

internal class IOSDatabaseUtil : DatabaseUtil {
    override fun generateUUID(): String = NSUUID().UUIDString()
}
