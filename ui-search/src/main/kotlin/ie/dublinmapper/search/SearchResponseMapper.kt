package ie.dublinmapper.search

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.setSearchCandidate
import ie.dublinmapper.ui.R
import io.rtpi.api.DockLocation
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation

object SearchResponseMapper {

    fun map(
        searchResultsResponse: SearchResultsResponse?,
        recentSearchesResponse: RecentSearchesResponse?,
        nearbyLocationsResponse: NearbyLocationsResponse?
    ) = Section(
        listOfNotNull(
            mapSearchResults(searchResultsResponse),
            mapRecentSearches(recentSearchesResponse, searchResultsResponse),
            mapNearbyLocations(nearbyLocationsResponse, searchResultsResponse)
        )
    )

    private fun mapSearchResults(searchResultsResponse: SearchResultsResponse?): Group? {
        return if (searchResultsResponse != null) {
            when (searchResultsResponse) {
                is SearchResultsResponse.Data -> Section(
                    searchResultsResponse.serviceLocations.flatMap { serviceLocation ->
                        mapServiceLocation(serviceLocation, null)
                    }
                )
                is SearchResultsResponse.NoResults -> Section(NoSearchResultsItem(searchResultsResponse.query, 23421))
                is SearchResultsResponse.Empty -> Section()
            }
        } else {
            null
        }
    }

    private fun mapRecentSearches(
        recentSearchesResponse: RecentSearchesResponse?,
        searchResultsResponse: SearchResultsResponse?
    ): Group? {
        return if (
            recentSearchesResponse != null &&
            (searchResultsResponse == null || searchResultsResponse is SearchResultsResponse.Empty)
        ) {
            return Section(
                listOf(
                    HeaderItem(message = "Recent searches", drawableId = R.drawable.ic_clock, index = 3220023)
                ).plus(
                    when (recentSearchesResponse) {
                        is RecentSearchesResponse.Data -> recentSearchesResponse.serviceLocations.flatMap { serviceLocation ->
                            mapServiceLocation(serviceLocation, null)
                        }
                        is RecentSearchesResponse.Empty -> listOf(NoRecentSearchesItem(350033))
                    }
                )
            )
        } else {
            null
        }
    }

    private fun mapNearbyLocations(
        nearbyLocationsResponse: NearbyLocationsResponse?,
        searchResultsResponse: SearchResultsResponse?
    ): Group? {
        return if (
            nearbyLocationsResponse != null &&
            (searchResultsResponse == null || searchResultsResponse is SearchResultsResponse.Empty)
        ) {
            return Section(
                listOf(
                    HeaderItem(message = "Places near you", drawableId = R.drawable.ic_near_me, index = 94838)
                ).plus(
                    when (nearbyLocationsResponse) {
                        is NearbyLocationsResponse.Data -> Section(
                            nearbyLocationsResponse.serviceLocations.entries.flatMap { entry ->
                                mapServiceLocation(entry.value, entry.key)
                            }
                        )
                        is NearbyLocationsResponse.LocationDisabled -> Section(
                            NoLocationItem(7748)
                        )
                    }
                )
            )
        } else {
            null
        }
    }

    private fun mapServiceLocation(
        serviceLocation: ServiceLocation,
        walkDistance: Double?
    ): List<ServiceLocationItem> {
        return when (serviceLocation) {
            is StopLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
                    routeGroups = serviceLocation.routeGroups,
                    walkDistance = walkDistance
                ).apply {
                    setSearchCandidate()
                }
            )
            is DockLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
                    routeGroups = emptyList(),
                    walkDistance = walkDistance
                ).apply {
                    setSearchCandidate()
                }
            )
            else -> emptyList()
        }
    }

    private fun mapIcon(service: Service): Int = when (service) {
        Service.AIRCOACH,
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> R.drawable.ic_bus
        Service.DUBLIN_BIKES -> R.drawable.ic_bike
        Service.IRISH_RAIL -> R.drawable.ic_train
        Service.LUAS -> R.drawable.ic_tram
    }
}
