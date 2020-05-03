package ie.dublinmapper.search

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.DubLinkDockLocation
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.model.AbstractServiceLocationItem
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.DockLocationItem
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.StopLocationItem
import ie.dublinmapper.model.setSearchCandidate
import ie.dublinmapper.ui.R

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
        serviceLocation: DubLinkServiceLocation,
        walkDistance: Double?
    ): List<AbstractServiceLocationItem> {
        return when (serviceLocation) {
            is DubLinkStopLocation -> listOf(
                StopLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = walkDistance
                ).apply {
                    setSearchCandidate()
                }
            )
            is DubLinkDockLocation -> listOf(
                DockLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = walkDistance
                ).apply {
//                    setSearchCandidate() // TODO
                }
            )
            else -> emptyList()
        }
    }
}
