package io.dublink.nearby

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.LiveDataKey
import io.dublink.domain.repository.LiveDataRepository
import io.dublink.domain.repository.LiveDataResponse
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.util.AppConstants
import io.dublink.domain.util.haversine
import io.dublink.domain.util.truncateHead
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import io.rtpi.api.LiveData
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: LiveDataRepository,
    private val preferenceStore: PreferenceStore
) {
    fun getNearbyServiceLocations(coordinate: Coordinate): Observable<List<DubLinkServiceLocation>> {
        return serviceLocationRepository.getNearest(coordinate, limit = AppConstants.maxNearbyLocations)
            .map { response -> response.serviceLocations
                .associateBy { it.coordinate.haversine(coordinate) }
                .toSortedMap()
                .truncateHead(AppConstants.maxNearbyLocations)
                .values
                .toList()
            }
    }

    fun getLiveData(serviceLocation: DubLinkServiceLocation): Observable<List<LiveData>> {
        return liveDataRepository.get(
            LiveDataKey(
                service = serviceLocation.service,
                locationId = serviceLocation.id
            ),
            refresh = false
        ).map {
            if (it is LiveDataResponse.Data) {
                return@map it.liveData
            } else {
                return@map emptyList<LiveData>()
            }
        }
    }
}
