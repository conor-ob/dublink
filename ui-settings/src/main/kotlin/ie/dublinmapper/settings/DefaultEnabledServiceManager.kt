package ie.dublinmapper.settings

import ie.dublinmapper.util.EnabledServiceManager
import ie.dublinmapper.util.PreferenceStore
import io.rtpi.api.Service
import javax.inject.Inject

class DefaultEnabledServiceManager @Inject constructor(
    private val preferenceStore: PreferenceStore,
    private val serviceToEnabledServicePreferenceKey: Map<Service, String>
) : EnabledServiceManager {

    override fun enableService(service: Service) {
        val result = preferenceStore.setBoolean(serviceToEnabledServicePreferenceKey.getValue(service), true)
    }

    override fun isServiceEnabled(service: Service): Boolean {
        return preferenceStore.getBoolean(serviceToEnabledServicePreferenceKey.getValue(service), true)
    }

    override fun getEnabledServices(): Set<Service> {
        return Service.values().filter { isServiceEnabled(it) }.toSet()
    }

}
