package io.dublink.domain.service

interface InternetManager {

    fun isConnected(): Boolean

    fun isNetworkAvailable(): Boolean

    fun isWiFiAvailable(): Boolean
}
