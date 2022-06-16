package br.com.corelib.calls

import retrofit2.Response

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ResponseAny<T> {
    return try {
        ResponseAny.create(call())
    } catch (e: Exception) {
        ResponseAny.create(e)
    }
}