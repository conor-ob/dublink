package ie.dublinmapper.internet

import ie.dublinmapper.domain.service.InternetManager
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(
    private val internetManager: InternetManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (internetManager.isConnected()) {
            val builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        } else {
            throw NetworkUnavailableException()
        }
    }
}
