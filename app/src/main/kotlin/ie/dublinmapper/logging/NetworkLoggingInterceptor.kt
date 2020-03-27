package ie.dublinmapper.logging

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class NetworkLoggingInterceptor : Interceptor {

    private val httpLoggingInterceptor: Interceptor by lazy { newHttpLoggingInterceptor() }

    override fun intercept(chain: Interceptor.Chain): Response = httpLoggingInterceptor.intercept(chain)

    private fun newHttpLoggingInterceptor() = HttpLoggingInterceptor(
        HttpLoggingInterceptor.Logger {
            Timber.d(it)
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
}
