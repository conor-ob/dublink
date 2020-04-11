package ie.dublinmapper.search

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.model.LoadingItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.SimpleMessageItem
import ie.dublinmapper.model.setSearchCandidate
import ie.dublinmapper.ui.R
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object SearchResponseMapper {

    fun map(
        nearbyLocationsResponse: NearbyLocationsResponse?,
        searchResultsResponse: SearchResultsResponse?,
        recentSearchesResponse: RecentSearchesResponse?
    ) = Section(
        listOfNotNull(
            mapRecentSearches(recentSearchesResponse, searchResultsResponse),
            mapNearbyLocations(nearbyLocationsResponse, searchResultsResponse),
            mapSearchResults(searchResultsResponse)
        )
    )

    private fun mapRecentSearches(
        recentSearchesResponse: RecentSearchesResponse?,
        searchResultsResponse: SearchResultsResponse?
    ): Group? {
        return if (recentSearchesResponse != null && (searchResultsResponse == null || searchResultsResponse.serviceLocations.isNullOrEmpty())) {
            return Section(
                SimpleMessageItem("Recent searches", 6),
                recentSearchesResponse.serviceLocations.flatMap { serviceLocation ->
                    when (serviceLocation) {
                        is StopLocation -> listOf(
                            ServiceLocationItem(
                                serviceLocation = serviceLocation,
                                icon = mapIcon(serviceLocation.service),
                                routeGroups = serviceLocation.routeGroups,
                                walkDistance = null
                            ).apply {
                                setSearchCandidate()
                            }
                        )
                        is DockLocation -> listOf(
                            ServiceLocationItem(
                                serviceLocation = serviceLocation,
                                icon = mapIcon(serviceLocation.service),
                                routeGroups = emptyList(),
                                walkDistance = null
                            ).apply {
                                setSearchCandidate()
                            }
                        )
                        else -> emptyList()
                    }
                }
            )
        } else {
            null
        }
    }

    private fun mapNearbyLocations(
        nearbyLocationsResponse: NearbyLocationsResponse?,
        searchResultsResponse: SearchResultsResponse?
    ): Group? {
        return if (nearbyLocationsResponse != null && (searchResultsResponse == null || searchResultsResponse.serviceLocations.isNullOrEmpty())) {
            when (nearbyLocationsResponse) {
                is NearbyLocationsResponse.Data -> Section(
                    SimpleMessageItem("Places near you", 1),
//                    LoadingItem(),
                    nearbyLocationsResponse.serviceLocations.entries.flatMap { entry ->
                        when (val serviceLocation = entry.value) {
                            is StopLocation -> listOf(
                                ServiceLocationItem(
                                    serviceLocation = serviceLocation,
                                    icon = mapIcon(serviceLocation.service),
                                    routeGroups = serviceLocation.routeGroups,
                                    walkDistance = entry.key
                                ).apply {
                                    setSearchCandidate()
                                }
                            )
                            is DockLocation -> listOf(
                                ServiceLocationItem(
                                    serviceLocation = serviceLocation,
                                    icon = mapIcon(serviceLocation.service),
                                    routeGroups = emptyList(),
                                    walkDistance = entry.key
                                ).apply {
                                    setSearchCandidate()
                                }
                            )
                            else -> emptyList()
                        }
                    }
                )
                is NearbyLocationsResponse.LocationDisabled -> Section(
                    SimpleMessageItem("Enable your location", 3)
                )
            }
        } else {
            null
        }
    }

    private fun mapSearchResults(searchResultsResponse: SearchResultsResponse?): Group? {
        if (searchResultsResponse != null && searchResultsResponse.serviceLocations.isNotEmpty()) {
            return Section(
                searchResultsResponse.serviceLocations.flatMap { serviceLocation ->
                    when (serviceLocation) {
                        is StopLocation -> listOf(
                            ServiceLocationItem(
                                serviceLocation = serviceLocation,
                                icon = mapIcon(serviceLocation.service),
                                routeGroups = serviceLocation.routeGroups,
                                walkDistance = null
                            ).apply {
                                setSearchCandidate()
                            }
                        )
                        is DockLocation -> listOf(
                            ServiceLocationItem(
                                serviceLocation = serviceLocation,
                                icon = mapIcon(serviceLocation.service),
                                routeGroups = emptyList(),
                                walkDistance = null
                            ).apply {
                                setSearchCandidate()
                            }
                        )
                        else -> emptyList()
                    }
                }
            )
        } else {
            return null
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
