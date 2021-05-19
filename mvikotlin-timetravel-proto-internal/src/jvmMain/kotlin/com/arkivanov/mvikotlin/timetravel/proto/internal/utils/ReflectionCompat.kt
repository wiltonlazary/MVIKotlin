package com.arkivanov.mvikotlin.timetravel.proto.internal.utils

import java.lang.reflect.Field

internal actual fun Field.canAccessCompat(obj: Any?): Boolean = canAccess(obj)

internal actual fun Field.trySetAccessibleCompat(): Boolean = trySetAccessible()
