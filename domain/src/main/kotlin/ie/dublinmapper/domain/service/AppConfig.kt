package ie.dublinmapper.domain.service

interface AppConfig {

    fun isDebug(): Boolean

    fun appVersion(): String
}
