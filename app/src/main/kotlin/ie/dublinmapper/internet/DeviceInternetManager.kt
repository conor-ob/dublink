package ie.dublinmapper.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.SupplicantState
import ie.dublinmapper.domain.service.InternetManager
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.net.wifi.WifiManager as AndroidWifiManager

class DeviceInternetManager(private val context: Context) : InternetManager {

    override fun isConnected(): Boolean {
        if (isNetworkAvailable()) {
            try {
                val connection = URL("http://clients3.google.com/generate_204")
                    .openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Android")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1000
                connection.connect()
                val result = connection.responseCode == 204 && connection.contentLength == 0
                connection.disconnect()
                return result
            } catch (e: IOException) {
                Timber.e(e)
            }
        } else {
            Timber.d("No network available")
        }
        return false
    }

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
        return false
    }

    override fun isWiFiAvailable(): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as AndroidWifiManager?
        if (wifiManager != null && wifiManager.isWifiEnabled) {
            return wifiManager.connectionInfo.supplicantState == SupplicantState.COMPLETED
        }
        return false
    }
}
