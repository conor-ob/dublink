package io.dublink.search

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.model.AbstractServiceLocationItem
import io.dublink.model.DividerItem
import io.dublink.model.DockLocationItem
import io.dublink.model.HeaderItem
import io.dublink.model.StopLocationItem
import io.dublink.model.setSearchCandidate
import io.dublink.ui.R

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
            mapRecentSearches(
                recentSearchesResponse,
                searchResultsResponse,
                nearbyLocationsResponse,
                clearRecentSearchesClickListener
            ),
            mapNearbyLocations(
                nearbyLocationsResponse,
                searchResultsResponse,
                onEnableLocationClickedListener
            )
        )
    )

    private fun mapSearchResults(searchResultsResponse: SearchResultsResponse?): Group? {
        return if (searchResultsResponse != null) {
            when (searchResultsResponse) {
                is SearchResultsResponse.Data -> Section(
                    searchResultsResponse.serviceLocations.flatMap { serviceLocation ->
                        mapServiceLocation(
                            serviceLocation,
                            null,
                            true
                        )
                    }
                )
                is SearchResultsResponse.NoResults -> Section(
                    NoSearchResultsItem(
                        id = 23421L,
                        query = searchResultsResponse.query
                    )
                )
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
                        HeaderItem(
                            message = "Recent searches",
                            drawableId = R.drawable.ic_history,
                            index = 3220023
                        )
                    )
                        .plus(
                            recentSearchesResponse.serviceLocations.flatMap { serviceLocation ->
                                mapServiceLocation(
                                    serviceLocation,
                                    null,
                                    false
                                )
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
                        HeaderItem(
                            message = "Recent searches",
                            drawableId = R.drawable.ic_history,
                            index = 3220023
                        )
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
                        HeaderItem(
                            message = "Places near you",
                            drawableId = R.drawable.ic_near_me,
                            index = 94838
                        )
                    )
                        .plus(
                            nearbyLocationsResponse.serviceLocations.entries.flatMap { entry ->
                                mapServiceLocation(
                                    entry.value,
                                    entry.key,
                                    false
                                )
                            }
                        )
                )
                is NearbyLocationsResponse.LocationDisabled -> Section(
                    listOf(
                        HeaderItem(
                            message = "Places near you",
                            drawableId = R.drawable.ic_near_me,
                            index = 94838
                        ),
                        NoLocationItem(
                            id = 7748L,
                            clickListener = onEnableLocationClickedListener
                        )
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
        walkDistance: Double?,
        searchResult: Boolean
    ): List<AbstractServiceLocationItem> {
        return when (serviceLocation) {
            is DubLinkStopLocation -> listOf(
                StopLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = walkDistance
                ).apply {
                    if (searchResult) {
                        setSearchCandidate()
                    }
                }
            )
            is DubLinkDockLocation -> listOf(
                DockLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = walkDistance
                ).apply {
                    if (searchResult) {
                        setSearchCandidate()
                    }
                }
            )
            else -> emptyList()
        }
    }
}
