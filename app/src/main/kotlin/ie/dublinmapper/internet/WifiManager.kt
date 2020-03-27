package ie.dublinmapper.internet

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager as AndroidWifiManager
import ie.dublinmapper.core.InternetManager

class WifiManager(private val context: Context) : InternetManager {

    override fun isConnectedToWiFi(): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as AndroidWifiManager?
        if (wifiManager != null && wifiManager.isWifiEnabled) {
            return wifiManager.connectionInfo.supplicantState == SupplicantState.COMPLETED
        }
        return false
    }
}
