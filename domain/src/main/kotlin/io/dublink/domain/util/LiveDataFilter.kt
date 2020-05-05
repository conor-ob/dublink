package io.dublink.domain.util

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.Filter
import io.rtpi.api.LiveData
import io.rtpi.api.PredictionLiveData

object LiveDataFilter {

    fun filterLiveData(
        serviceLocation: DubLinkServiceLocation?,
        liveData: List<LiveData>
    ): List<LiveData> {
        if (serviceLocation is DubLinkStopLocation && liveData.all { it is PredictionLiveData }) {
            val predictionLiveData = liveData.map { it as PredictionLiveData }
            val activeRouteFilters = serviceLocation.filters
                .filterIsInstance<Filter.RouteFilter>()
                .filter { it.isActive }
            val activeDirectionFilters = serviceLocation.filters
                .filterIsInstance<Filter.DirectionFilter>()
                .filter { it.isActive }
            return if (activeRouteFilters.isEmpty() && activeDirectionFilters.isEmpty()) {
                predictionLiveData
            } else if (activeRouteFilters.isEmpty()) {
                predictionLiveData.filter {
                    activeDirectionFilters.map { filter -> filter.direction }.contains(it.routeInfo.direction)
                }
            } else if (activeDirectionFilters.isEmpty()) {
                predictionLiveData.filter {
                    activeRouteFilters.map { filter -> filter.route.id }.contains(it.routeInfo.route)
                }
            } else {
                predictionLiveData.filter {
                    activeRouteFilters.map { filter -> filter.route.id }.contains(it.routeInfo.route) &&
                        activeDirectionFilters.map { filter -> filter.direction }.contains(it.routeInfo.direction)
                }
            }
        } else {
            return liveData
        }
    }
}
