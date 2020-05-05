package io.dublink.domain.service

import io.rtpi.api.Service

interface EnabledServiceManager {

    fun enableService(service: Service): Boolean

    fun isServiceEnabled(service: Service): Boolean

    fun getEnabledServices(): List<Service>
}
