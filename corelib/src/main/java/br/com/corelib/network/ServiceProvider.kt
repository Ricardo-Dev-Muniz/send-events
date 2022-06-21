package br.com.corelib.network

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class ServiceProvider(
    private val url: String,
    private val timeout: Long = 90,
    private val headers: List<Pair<String, String>>? = null,
    private val callAdapter: CallAdapter.Factory? = RxJava2CallAdapterFactory.create()
) {

    val innerRetrofit by lazy {
        getService()
    }

    private fun getService(): Retrofit {
        val okClient = OkHttpClient.Builder().retryOnConnectionFailure(true)

        okClient.writeTimeout(timeout, TimeUnit.SECONDS)
        okClient.readTimeout(timeout, TimeUnit.SECONDS)

        okClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = chain.request().newBuilder()


            headers?.forEach {
                requestBuilder.header(it.first, it.second)
            }

            requestBuilder.addHeader("Connection", "close")

            requestBuilder.method(original.method, original.body)
            val response = chain.proceed(requestBuilder.build())

            response
        })

        okClient.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).addInterceptor(OkHttpProfilerInterceptor())

        okClient.networkInterceptors().add(ServiceInterceptor)

        return Retrofit.Builder().apply {
            baseUrl(url)
            client(okClient.build())
            addConverterFactory(GsonConverterFactory.create())
            callAdapter?.let { addCallAdapterFactory(it) }
        }.build()
    }

    inline fun <reified T> generate(): T {
        try {
            return innerRetrofit.create(T::class.java)
        } catch (e: Exception) {
            throw Exception("Not found service provider: ${T::class.java}/ $e")
        }
    }

}