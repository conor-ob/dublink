package ie.dublinmapper.util

interface EnabledServiceManager {

    fun enableService(service: Service)

    fun isServiceEnabled(service: Service): Boolean

    fun getEnabledServices(): Set<Service>

}
