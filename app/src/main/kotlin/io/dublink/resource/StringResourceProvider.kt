package io.dublink.resource

import android.content.res.Resources
import io.dublink.domain.internet.NetworkUnavailableException
import io.dublink.domain.repository.ServiceLocationResponse
import io.dublink.domain.service.StringProvider
import io.dublink.ui.R
import io.rtpi.api.Service
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

class StringResourceProvider(
    private val resources: Resources
) : StringProvider {

    override fun loadingMessage(): String {
        return resources.getString(R.string.message_loading)
    }

    override fun errorMessage(service: Service?, throwable: Throwable): String {
        return when (throwable) {
            // service is down
            is ConnectException -> if (service != null) {
                resources.getString(R.string.error_service, service.fullName)
            } else {
                resources.getString(R.string.error_general)
            }
            // user has no internet connection
            is NetworkUnavailableException,
            is UnknownHostException -> resources.getString(R.string.error_internet)
            // network error
            is IOException -> if (service != null) {
                resources.getString(R.string.error_io, service.fullName)
            } else {
                resources.getString(R.string.error_general)
            }
            else -> resources.getString(R.string.error_general)
        }
    }

    override fun errorMessage(errorResponses: List<ServiceLocationResponse.Error>): String {
        return when {
            errorResponses.all { errorResponse ->
                errorResponse.throwable is NetworkUnavailableException ||
                    errorResponse.throwable is UnknownHostException } -> {
                resources.getString(R.string.error_internet)
            }
            errorResponses.all { errorResponse ->
                errorResponse.throwable is ConnectException ||
                    errorResponse.throwable is IOException } -> {
                val errorServices = errorResponses.map { it.service }
                when (errorServices.size) {
                    1 -> "${errorServices.first()} is experiencing problems"
                    2 -> "${errorServices.joinToString(separator = " and ")} are experiencing problems"
                    else -> "${errorServices.take(errorServices.size - 1).joinToString(separator = ", ")} and ${errorServices.last()} are experiencing problems"
                }
            }
            else -> {
                resources.getString(R.string.error_general)
            }
        }
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
