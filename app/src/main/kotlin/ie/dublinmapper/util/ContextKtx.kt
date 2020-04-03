package ie.dublinmapper.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager

fun Context.getConnectivityManager() =
    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

fun Context.getWifiManager() =
    applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

fun ConnectivityManager.isConnected() = activeNetworkInfo?.isConnectedOrConnecting ?: false

fun WifiManager.isConnected() = isWifiEnabled &&
        connectionInfo.supplicantState == SupplicantState.COMPLETED
