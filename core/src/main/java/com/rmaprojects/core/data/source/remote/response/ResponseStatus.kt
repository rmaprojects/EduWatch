package com.rmaprojects.core.data.source.remote.response

sealed class ResponseStatus<out T> {
    object Loading: ResponseStatus<Nothing>()
    class Success<T>(val data: T): ResponseStatus<T>()
    data class Error(val message: String): ResponseStatus<Nothing>()
}
