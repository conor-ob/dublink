package io.dublink.domain.service

interface AppConfig {

    fun isDebug(): Boolean

    fun appVersion(): String

    fun publicKey(): String
}
