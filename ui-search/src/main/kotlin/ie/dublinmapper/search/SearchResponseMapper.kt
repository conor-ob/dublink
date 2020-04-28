package ie.dublinmapper.search

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.getCustomRoutes
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.model.DividerItem
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
        nearbyLocationsResponse: NearbyLocationsResponse?,
        onEnableLocationClickedListener: OnEnableLocationClickedListener,
        clearRecentSearchesClickListener: ClearRecentSearchesClickListener
    ) = Section(
        listOfNotNull(
            mapSearchResults(searchResultsResponse),
            mapRecentSearches(recentSearchesResponse, searchResultsResponse, nearbyLocationsResponse, clearRecentSearchesClickListener),
            mapNearbyLocations(nearbyLocationsResponse, searchResultsResponse, onEnableLocationClickedListener)
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
                is SearchResultsResponse.NoResults -> Section(NoSearchResultsItem(id = 23421L, query = searchResultsResponse.query))
                is SearchResultsResponse.Empty -> Section()
            }
        } else {
            null
        }
    }

    private fun mapRecentSearches(
        recentSearchesResponse: RecentSearchesResponse?,
        searchResultsResponse: SearchResultsResponse?,
        nearbyLocationsResponse: NearbyLocationsResponse?,
        clearRecentSearchesClickListener: ClearRecentSearchesClickListener
    ): Group? {
        return if (
            recentSearchesResponse != null &&
            (searchResultsResponse == null || searchResultsResponse is SearchResultsResponse.Empty)
        ) {
            return when (recentSearchesResponse) {
                is RecentSearchesResponse.Data -> Section(
                    listOf(
                        HeaderItem(message = "Recent searches", drawableId = R.drawable.ic_history, index = 3220023)
                    )
                        .plus(
                            recentSearchesResponse.serviceLocations.flatMap { serviceLocation ->
                                mapServiceLocation(serviceLocation, null)
                            }
                        )
                        .plus(
                            listOf(
                                ClearRecentSearchesItem(id = 9001L, clickListener = clearRecentSearchesClickListener),
                                DividerItem(id = 9247L)
                            )
                        )
                )
                is RecentSearchesResponse.Empty -> Section(
                    listOf(
                        HeaderItem(message = "Recent searches", drawableId = R.drawable.ic_history, index = 3220023)
                    )
                        .plus(
                            if (nearbyLocationsResponse is NearbyLocationsResponse.Hidden) {
                                listOf(
                                    NoRecentSearchesItem(id = 350033L)
                                )
                            } else {
                                listOf(
                                    NoRecentSearchesItem(id = 350033L),
                                    DividerItem(id = 9247L)
                                )
                            }
                        )
                )
                is RecentSearchesResponse.Hidden -> null
            }
        } else {
            null
        }
    }

    private fun mapNearbyLocations(
        nearbyLocationsResponse: NearbyLocationsResponse?,
        searchResultsResponse: SearchResultsResponse?,
        onEnableLocationClickedListener: OnEnableLocationClickedListener
    ): Group? {
        return if (
            nearbyLocationsResponse != null &&
            (searchResultsResponse == null || searchResultsResponse is SearchResultsResponse.Empty)
        ) {
            return when (nearbyLocationsResponse) {
                is NearbyLocationsResponse.Data -> Section(
                    listOf(
                        HeaderItem(message = "Places near you", drawableId = R.drawable.ic_near_me, index = 94838)
                    )
                        .plus(
                            nearbyLocationsResponse.serviceLocations.entries.flatMap { entry ->
                                mapServiceLocation(entry.value, entry.key)
                            }
                        )
                )
                is NearbyLocationsResponse.LocationDisabled -> Section(
                    listOf(
                        HeaderItem(message = "Places near you", drawableId = R.drawable.ic_near_me, index = 94838),
                        NoLocationItem(id = 7748L, clickListener = onEnableLocationClickedListener)
                    )
                )
                is NearbyLocationsResponse.Loading -> LoadingItem(id = 67683L)
                is NearbyLocationsResponse.Hidden -> null
            }
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
                    routeGroups = if (serviceLocation.isFavourite()) {
                        serviceLocation.getCustomRoutes()
                    } else {
                        serviceLocation.routeGroups
                    },
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
