package io.dublink.resource

import android.content.res.Resources
import io.dublink.BuildConfig
import io.dublink.domain.service.StringProvider
import io.dublink.ui.R
import io.rtpi.api.Service

class StringResourceProvider(
    private val resources: Resources
) : StringProvider {

    override fun jcDecauxApiKey(): String {
        return BuildConfig.JCDECAUX_API_KEY
    }

    override fun databaseName(): String {
        return resources.getString(R.string.database_name)
    }

    override fun noArrivalsMessage(service: Service?): String {
        val mode = when (service) {
            Service.AIRCOACH,
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS -> resources.getString(R.string.live_data_no_scheduled_arrivals_mode_bus)
            Service.IRISH_RAIL -> resources.getString(R.string.live_data_no_scheduled_arrivals_mode_train)
            Service.LUAS -> resources.getString(R.string.live_data_no_scheduled_arrivals_mode_tram)
            else -> resources.getString(R.string.live_data_no_scheduled_arrivals_mode_default)
        }
        return resources.getString(R.string.live_data_no_scheduled_arrivals, mode)
    }
}
