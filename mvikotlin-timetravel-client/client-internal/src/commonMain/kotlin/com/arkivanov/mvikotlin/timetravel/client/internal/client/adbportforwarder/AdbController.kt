package com.arkivanov.mvikotlin.timetravel.client.internal.client.adbportforwarder

interface AdbController {

    fun forwardPort(port: Int): Result

    sealed class Result {
        object Success : Result()
        data class Error(val text: String) : Result()
    }
}
