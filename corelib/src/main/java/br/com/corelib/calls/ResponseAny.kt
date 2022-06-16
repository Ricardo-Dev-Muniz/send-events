package br.com.corelib.calls

import retrofit2.Response
import retrofit2.HttpException
import java.io.IOException

sealed class ResponseAny<out T> {
    companion object {

        fun <T> ioException(response : Response<T>) = IOException(
            "ERROR CODE: ${response.code()} - ERROR MESSAGE: ${response.message()} - ERROR BODY: ${
                response.errorBody()?.string()
            }", HttpException(response)
        )

        fun <T> create(response: Response<T>): ResponseAny<T> {
            return if (response.isSuccessful) {
                val body = response.body()

                if (body == null || response.code() == 204) {
                    ResponseEmpty(response.code())
                } else {
                    ResponseSuccess(body)
                }
            } else {

                if (response.code() == 422 && response.errorBody() != null) {
                    response.errorBody()?.byteStream()
                        ?.bufferedReader()
                        ?.readLine().run {
                            ResponseError(ioException(response), this)
                        }
                } else {
                    ResponseError(
                        ioException(response)
                    )
                }

            }
        }

        fun create(exception: Exception): ResponseError = ResponseError(exception)

    }
}

data class ResponseError(val exception: Exception, val errorBody: String? = null) :
    ResponseAny<Nothing>()

data class ResponseEmpty(val code: Int) : ResponseAny<Nothing>()

data class ResponseSuccess<T>(val body: T) : ResponseAny<T>()