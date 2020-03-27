package ie.dublinmapper.core

interface AppConfig {

    fun isDebug(): Boolean

    fun appVersion(): String
}