package io.dublink.favourites.edit

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.repository.AggregatedServiceLocationRepository
import io.dublink.domain.repository.FavouriteRepository
import io.dublink.domain.repository.ServiceLocationResponse
import io.reactivex.Observable
import javax.inject.Inject

class EditFavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val serviceLocationRepository: AggregatedServiceLocationRepository
) {

    fun getFavourites(): Observable<FavouritesResponse> =
        serviceLocationRepository.getFavourites()
            .map<FavouritesResponse> { responses ->
                FavouritesResponse.Data(
                    serviceLocations = responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { response -> response.serviceLocations }
                        .sortedBy { serviceLocation -> serviceLocation.favouriteSortIndex }
                )
            }
            .onErrorReturn { throwable ->
                FavouritesResponse.Error(throwable)
            }

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>): Observable<Boolean> {
        return Observable.fromCallable {
            serviceLocationRepository.clearAllCaches()
            favouriteRepository.saveChanges(serviceLocations)
            true
        }
    }
}

sealed class FavouritesResponse {

    data class Data(
        val serviceLocations: List<DubLinkServiceLocation>
    ) : FavouritesResponse()

    data class Error(
        val throwable: Throwable
    ) : FavouritesResponse()
}
