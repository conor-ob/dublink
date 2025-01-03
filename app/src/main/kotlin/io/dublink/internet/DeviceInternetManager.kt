package io.dublink.internet

import android.content.Context
import io.dublink.domain.service.InternetManager
import io.dublink.util.getConnectivityManager
import io.dublink.util.getWifiManager
import io.dublink.util.isConnected
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import timber.log.Timber

class DeviceInternetManager(private val context: Context) : InternetManager {

    private val googleServers = listOf(
        "http://connectivitycheck.gstatic.com/generate_204",
        "http://clients3.google.com/generate_204",
        "http://clients1.google.com/generate_204",
        "http://clients2.google.com/generate_204",
        "http://clients4.google.com/generate_204",
        "http://clients5.google.com/generate_204",
        "http://clients6.google.com/generate_204",
        "http://www.google.com/gen_204",
        "http://www.gstatic.com/generate_204",
        "http://maps.google.com/generate_204",
        "http://mt0.google.com/generate_204",
        "http://mt1.google.com/generate_204",
        "http://mt2.google.com/generate_204",
        "http://mt3.google.com/generate_204",
        "https://www.gstatic.com/generate_204",
        "https://gstatic.com/generate_204"
    )

    override fun isConnected() = if (isNetworkAvailable()) {
        googleServers
            .asSequence()
            .map { tryPing(it) }
            .first { it }
    } else {
        false
    }

    private fun tryPing(server: String): Boolean {
        var urlConnection: HttpURLConnection? = null
        try {
            Timber.d("Attempting to ping $server")
            urlConnection = URL(server).openConnection() as HttpURLConnection
//            urlConnection.instanceFollowRedirects = false
            urlConnection.useCaches = false
//            urlConnection.allowUserInteraction = false
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Android")
            urlConnection.setRequestProperty("Connection", "close")
            urlConnection.connectTimeout = 10000
            urlConnection.readTimeout = 10000
            urlConnection.connect()
            val successful = urlConnection.responseCode == 204 && urlConnection.contentLength == 0
            if (successful) {
                Timber.d("Pinged $server")
                return successful
            }
        } catch (e: IOException) {
            Timber.e(e, "Error while pinging $server")
        } catch (e: Exception) {
            Timber.e(e, "Error while pinging $server")
        } finally {
            urlConnection?.disconnect()
        }
        Timber.d("Failed to ping $server")
        return false
    }

    override fun isNetworkAvailable() = context.getConnectivityManager()?.isConnected() ?: false

    override fun isWiFiAvailable() = context.getWifiManager()?.isConnected() ?: false
}
