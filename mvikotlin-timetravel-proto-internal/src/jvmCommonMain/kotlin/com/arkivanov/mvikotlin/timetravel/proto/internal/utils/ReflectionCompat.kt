package com.arkivanov.mvikotlin.timetravel.proto.internal.utils

import java.lang.reflect.Field

internal expect fun Field.canAccessCompat(obj: Any?): Boolean

internal expect fun Field.trySetAccessibleCompat(): Boolean
