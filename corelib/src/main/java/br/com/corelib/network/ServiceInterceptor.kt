package br.com.corelib.network

import okhttp3.Interceptor
import okhttp3.Response

object ServiceInterceptor : Interceptor {
    private var onServiceResponseListener: OnServiceResponseListener? = null

    fun setListener(listener: OnServiceResponseListener) {
        onServiceResponseListener = listener
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        onServiceResponseListener?.onReceiveResponseCode(response.code)
        return response
    }

    interface OnServiceResponseListener {
        fun onReceiveResponseCode(code: Int)
    }
}