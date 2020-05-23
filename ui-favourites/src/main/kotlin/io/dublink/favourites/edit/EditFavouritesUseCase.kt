package io.dublink.favourites.edit

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.setCustomSortIndex
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
                    serviceLocations = responses.serviceLocations
                        .sortedBy { serviceLocation -> serviceLocation.favouriteSortIndex },
                    errorResponses = responses.errorResponses
                )
            }
            .onErrorReturn { throwable ->
                FavouritesResponse.Error(throwable)
            }

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>): Observable<Boolean> {
        return Observable.fromCallable {
            serviceLocationRepository.clearAllCaches()
            favouriteRepository.saveFavourites(
                serviceLocations.mapIndexed { index, serviceLocation ->
                    serviceLocation.setCustomSortIndex(index)
                },
                overwrite = true
            )
            true
        }
    }
}

sealed class FavouritesResponse {

    data class Data(
        val serviceLocations: List<DubLinkServiceLocation>,
        val errorResponses: List<ServiceLocationResponse.Error>
    ) : FavouritesResponse()

    data class Error(
        val throwable: Throwable
    ) : FavouritesResponse()
}
