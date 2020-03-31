package ie.dublinmapper.settings

import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.domain.service.PreferenceStore
import io.rtpi.api.Service
import javax.inject.Inject

class DefaultEnabledServiceManager @Inject constructor(
    private val preferenceStore: PreferenceStore
) : EnabledServiceManager {

    override fun enableService(service: Service) = preferenceStore.setServiceEnabled(service)

    override fun isServiceEnabled(service: Service) = preferenceStore.isServiceEnabled(service)

    override fun getEnabledServices() = Service.values().filter { isServiceEnabled(it) }.toSet()
}
