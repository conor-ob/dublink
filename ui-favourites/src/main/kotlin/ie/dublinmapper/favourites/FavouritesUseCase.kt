package ie.dublinmapper.favourites

import ie.dublinmapper.domain.repository.*
import ie.dublinmapper.domain.service.PreferenceStore
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.ServiceLocation
import io.rtpi.util.LiveDataGrouper
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository,
    private val liveDataRepository: AggregatedLiveDataRepository,
    private val preferenceStore: PreferenceStore
) {

    fun getFavouritesWithLiveData(): Observable<List<LiveDataPresentationResponse>> {
        val limit = preferenceStore.getFavouritesLiveDataLimit()
        return serviceLocationRepository.getFavourites()
            .flatMap { responses ->
                Observable.combineLatest(
                    responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { it.serviceLocations }
//                    .sortedBy { it.id }
                        .mapIndexed { index: Int, serviceLocation: ServiceLocation ->
                            if (index < limit) {
                                getGroupedLiveData(serviceLocation)
                                    .startWith(
                                        LiveDataPresentationResponse.Loading(
                                            serviceLocation = serviceLocation
                                        )
                                    )
                            } else {
                                Observable.just(
                                    LiveDataPresentationResponse.Skipped(
                                        serviceLocation = serviceLocation
                                    )
                                )
                            }
                        }
                ) { streams -> streams.map { it as LiveDataPresentationResponse } }
            }
    }

    private fun getGroupedLiveData(
        serviceLocation: ServiceLocation
    ): Observable<LiveDataPresentationResponse> {
        return Observable
            .interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
            .flatMap {
                liveDataRepository.get(
                    LiveDataKey(
                        service = serviceLocation.service,
                        locationId = serviceLocation.id
                    )
                ).map { response ->
                    when (response) {
                        is LiveDataResponse.Data -> LiveDataPresentationResponse.Data(
                            serviceLocation = serviceLocation,
                            liveData = LiveDataGrouper.groupLiveData(response.liveData)
                        )
                        is LiveDataResponse.Error -> LiveDataPresentationResponse.Error(
                            serviceLocation = serviceLocation,
                            throwable = response.throwable
                        )
                    }
                }
            }
    }
}

sealed class LiveDataPresentationResponse {

    abstract val serviceLocation: ServiceLocation

    data class Loading(
        override val serviceLocation: ServiceLocation
    ) : LiveDataPresentationResponse()

    data class Skipped(
        override val serviceLocation: ServiceLocation
    ) : LiveDataPresentationResponse()

    data class Data(
        override val serviceLocation: ServiceLocation,
        val liveData: List<List<LiveData>>
    ) : LiveDataPresentationResponse()

    data class Error(
        override val serviceLocation: ServiceLocation,
        val throwable: Throwable
    ) : LiveDataPresentationResponse()
}
