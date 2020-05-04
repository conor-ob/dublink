package ie.dublinmapper.favourites.edit

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.ServiceLocationResponse
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
