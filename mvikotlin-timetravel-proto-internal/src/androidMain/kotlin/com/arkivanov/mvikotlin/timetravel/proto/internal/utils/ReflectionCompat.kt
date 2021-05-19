package com.arkivanov.mvikotlin.timetravel.proto.internal.utils

import java.lang.reflect.Field

internal actual fun Field.canAccessCompat(obj: Any?): Boolean = isAccessible

internal actual fun Field.trySetAccessibleCompat(): Boolean =
    try {
        isAccessible = true
        true
    } catch (e: SecurityException) {
        false
    }
