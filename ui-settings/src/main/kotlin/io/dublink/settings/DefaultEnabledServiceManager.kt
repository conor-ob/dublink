package io.dublink.settings

import io.dublink.domain.service.EnabledServiceManager
import io.dublink.domain.service.PreferenceStore
import io.rtpi.api.Service
import javax.inject.Inject

class DefaultEnabledServiceManager @Inject constructor(
    private val preferenceStore: PreferenceStore
) : EnabledServiceManager {

    override fun enableService(service: Service) = preferenceStore.setServiceEnabled(service)

    override fun isServiceEnabled(service: Service) = preferenceStore.isServiceEnabled(service)

//    override fun getEnabledServices() = Service.values().filter { isServiceEnabled(it) }.toSet()

    override fun getEnabledServices(): List<Service> {
        val customOrder = listOf(
            Service.LUAS,
            Service.IRISH_RAIL,
            Service.DUBLIN_BIKES,
            Service.DUBLIN_BUS,
//            Service.AIRCOACH,
            Service.BUS_EIREANN
        )
        return customOrder.filter { isServiceEnabled(it) }
    }
}
