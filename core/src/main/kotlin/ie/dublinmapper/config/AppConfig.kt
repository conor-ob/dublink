package ie.dublinmapper.config

interface AppConfig {

    fun isDebug(): Boolean

    fun appVersion(): String
}