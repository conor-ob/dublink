package ie.dublinmapper.logging

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class NetworkLoggingInterceptor : Interceptor {

    private val httpLoggingInterceptor: Interceptor by lazy { newHttpLoggingInterceptor() }

    override fun intercept(chain: Interceptor.Chain): Response = httpLoggingInterceptor.intercept(chain)

    private fun newHttpLoggingInterceptor() = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.d(message)
            }
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
}
