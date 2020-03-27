package ie.dublinmapper.core

import io.rtpi.api.Service

interface EnabledServiceManager {

    fun enableService(service: Service)

    fun isServiceEnabled(service: Service): Boolean

    fun getEnabledServices(): Set<Service>
}