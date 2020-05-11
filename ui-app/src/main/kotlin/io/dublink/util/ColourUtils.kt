package io.dublink.util

import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.ui.R
import io.rtpi.api.LiveData
import io.rtpi.api.Operator
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.Service

object ColourUtils {

    fun mapColours(serviceLocation: DubLinkServiceLocation): List<Int> {
        return when (serviceLocation) {
            is DubLinkStopLocation -> mapColours(serviceLocation)
            is DubLinkDockLocation -> listOf(mapColour(serviceLocation.service))
            else -> listOf(R.color.color_on_surface)
        }
    }

    fun mapColour(liveData: LiveData): Int {
        return when (liveData.operator) {
            Operator.AIRCOACH -> R.color.aircoach_brand
            Operator.BUS_EIREANN -> R.color.bus_eireann_brand
            Operator.COMMUTER -> R.color.commuter_brand
            Operator.DART -> R.color.dart_brand
            Operator.DUBLIN_BIKES -> R.color.dublin_bikes_brand
            Operator.DUBLIN_BUS -> R.color.dublin_bus_brand
            Operator.GO_AHEAD -> R.color.go_ahead_brand
            Operator.INTERCITY -> R.color.intercity_brand
            Operator.LUAS -> {
                if (liveData is PredictionLiveData) {
                    when (liveData.routeInfo.route) {
                        "Green", "Green Line" -> R.color.luas_green_brand
                        "Red", "Red Line" -> R.color.luas_red_brand
                        else -> R.color.color_on_surface
                    }
                } else {
                    R.color.color_on_surface
                }
            }
        }
    }

    private fun mapColours(stopLocation: DubLinkStopLocation): List<Int> {
        return if (stopLocation.isFavourite && stopLocation.favouriteMetadata != null) {
            requireNotNull(stopLocation.favouriteMetadata).routes
                .map { it.operator }
                .map { mapColour(it) }
        } else {
            stopLocation.stopLocation.routeGroups
                .map { it.operator }
                .map { mapColour(it) }
        }
    }

    private fun mapColour(service: Service): Int {
        return when (service) {
            Service.AIRCOACH -> R.color.aircoach_brand
            Service.BUS_EIREANN -> R.color.bus_eireann_brand
            Service.DUBLIN_BIKES -> R.color.dublin_bikes_brand
            Service.DUBLIN_BUS -> R.color.dublin_bus_brand
            Service.IRISH_RAIL -> R.color.color_on_surface // TODO
            Service.LUAS -> R.color.color_on_surface // TODO
        }
    }

    private fun mapColour(operator: Operator): Int {
        return when (operator) {
            Operator.AIRCOACH -> R.color.aircoach_brand
            Operator.BUS_EIREANN -> R.color.bus_eireann_brand
            Operator.COMMUTER -> R.color.commuter_brand
            Operator.DART -> R.color.dart_brand
            Operator.DUBLIN_BIKES -> R.color.dublin_bikes_brand
            Operator.DUBLIN_BUS -> R.color.dublin_bus_brand
            Operator.GO_AHEAD -> R.color.go_ahead_brand
            Operator.INTERCITY -> R.color.intercity_brand
            Operator.LUAS -> R.color.color_on_surface // TODO
        }
    }
}
