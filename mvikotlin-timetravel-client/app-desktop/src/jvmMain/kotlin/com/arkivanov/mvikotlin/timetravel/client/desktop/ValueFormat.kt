package com.arkivanov.mvikotlin.timetravel.client.desktop

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value

val Value.text: String
    get() {
        val builder = StringBuilder()
        builder.appendValueLine(indent = 0, prefix = null, value = this)

        return builder.toString()
    }

private fun Appendable.appendValueLine(indent: Int, prefix: String?, value: Value) {
    fun concatTypeAndValue(type: String, value: Any?): String =
        buildString {
            if (prefix != null) {
                append(prefix).append(": ")
            }

            if (value == null) {
                append(type)
            } else {
                if (prefix != null) {
                    append(type).append(" = ")
                }
                append(value)
            }
        }

    return when (value) {
        is Value.Primitive -> {
            val text =
                when (value) {
                    is Value.Primitive.Int -> concatTypeAndValue("Int", value.value)
                    is Value.Primitive.Long -> concatTypeAndValue("Long", value.value)
                    is Value.Primitive.Short -> concatTypeAndValue("Short", value.value)
                    is Value.Primitive.Byte -> concatTypeAndValue("Byte", value.value)
                    is Value.Primitive.Float -> concatTypeAndValue("Float", value.value)
                    is Value.Primitive.Double -> concatTypeAndValue("Double", value.value)
                    is Value.Primitive.Char -> concatTypeAndValue("Char", value.value)
                    is Value.Primitive.Boolean -> concatTypeAndValue("Boolean", value.value)
                }

            appendLine(indent, text)
        }

        is Value.Object.String -> appendLine(indent, concatTypeAndValue("String", "\"${value.value}\""))
        is Value.Object.IntArray -> appendLine(indent, "IntArray")
        is Value.Object.LongArray -> appendLine(indent, "LongArray")
        is Value.Object.ShortArray -> appendLine(indent, "ShortArray")
        is Value.Object.ByteArray -> appendLine(indent, "ByteArray")
        is Value.Object.FloatArray -> appendLine(indent, "FloatArray")
        is Value.Object.DoubleArray -> appendLine(indent, "DoubleArray")
        is Value.Object.CharArray -> appendLine(indent, "CharArray")
        is Value.Object.BooleanArray -> appendLine(indent, "BooleanArray")
        is Value.Object.Array -> appendLine(indent, "Array")

        is Value.Object.Iterable -> {
            appendLine(indent, concatTypeAndValue(value.type, null))
            value.value?.forEachIndexed { itemIndex, itemValue ->
                appendValueLine(indent = indent + 1, prefix = "[$itemIndex]", value = itemValue)
            }
            Unit
        }

        is Value.Object.Map -> appendLine(indent, "Map")

        is Value.Object.Other -> {
            appendLine(indent, concatTypeAndValue(value.type, null))
            value.value?.forEach { (name, value) ->
                appendValueLine(indent = indent + 1, prefix = name, value = value)
            }
            Unit
        }

        is Value.Object.Unparsed -> appendLine(indent, concatTypeAndValue(value.type, value.value))
    }
}

private fun Appendable.appendLine(indent: Int, value: CharSequence) {
    appendLine("${" ".repeat(indent * 4)}$value")
}
