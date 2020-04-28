package ie.dublinmapper.favourites.edit

import ie.dublinmapper.domain.model.getSortIndex
import ie.dublinmapper.domain.repository.AggregatedServiceLocationRepository
import ie.dublinmapper.domain.repository.FavouriteRepository
import ie.dublinmapper.domain.repository.ServiceLocationResponse
import io.reactivex.Observable
import io.rtpi.api.ServiceLocation
import javax.inject.Inject

class EditFavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val serviceLocationRepository: AggregatedServiceLocationRepository
) {

    fun getFavouriteLocations(): Observable<FavouritesResponse> =
        serviceLocationRepository.getFavourites()
            .map<FavouritesResponse> { responses ->
                FavouritesResponse.Data(
                    serviceLocations = responses
                        .filterIsInstance<ServiceLocationResponse.Data>()
                        .flatMap { response -> response.serviceLocations }
                        .sortedBy { serviceLocation -> serviceLocation.getSortIndex() }
                )
            }
            .onErrorReturn { throwable ->
                FavouritesResponse.Error(throwable)
            }

    fun saveChanges(serviceLocations: List<ServiceLocation>): Observable<Boolean> {
        return Observable.fromCallable {
            serviceLocationRepository.clearAllCaches()
            favouriteRepository.saveChanges(serviceLocations)
            true
        }
    }
}

sealed class FavouritesResponse {

    data class Data(
        val serviceLocations: List<ServiceLocation>
    ) : FavouritesResponse()

    data class Error(
        val throwable: Throwable
    ) : FavouritesResponse()
}
