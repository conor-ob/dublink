package io.dublink.favourites

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.id
import io.dublink.domain.service.StringProvider
import io.dublink.domain.util.AppConstants
import io.dublink.model.DividerItem
import io.dublink.model.DublinBikesLiveDataItem
import io.dublink.model.GroupedLiveDataItem
import io.dublink.model.SimpleMessageItem
import io.dublink.model.SimpleServiceLocationItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import javax.inject.Inject
import timber.log.Timber

class FavouritesMapper @Inject constructor(
    private val stringProvider: StringProvider
) {

    fun map(
        locationResponses: List<ServiceLocationPresentationResponse>?,
        liveData: List<LiveDataPresentationResponse>?
    ): Group {
        return if (locationResponses != null && locationResponses.isNotEmpty()) {
            Section(
                locationResponses.mapIndexed { index, locationResponse ->
                    Section(
                        listOfNotNull(
                            mapServiceLocation(
                                locationResponse
                            ),
                            mapLiveData(
                                locationResponse.serviceLocation,
                                liveData,
                                index
                            ),
                            mapDivider(
                                locationResponses.size,
                                locationResponse.serviceLocation,
                                index
                            )
                        )
                    )
                }
            )
        } else if (locationResponses != null && locationResponses.isEmpty()) {
            Section(NoFavouritesItem(1L))
        } else {
            Section(emptyList())
        }
    }

    private fun mapLiveData(
        favourite: DubLinkServiceLocation,
        liveData: List<LiveDataPresentationResponse>?,
        index: Int
    ): Section {
        return if (liveData == null) {
            Section(SimpleMessageItem(stringProvider.loadingMessage(), favourite.id()))
        } else {
            val match = liveData.find {
                it.serviceLocation.service == favourite.service &&
                it.serviceLocation.id == favourite.id
            }
            if (match == null) {
                Section(SimpleMessageItem(stringProvider.loadingMessage(), favourite.id()))
            } else {
                mapLiveData(match, favourite.id())
            }
        }
    }

    private fun mapLiveData(
        liveDataResponse: LiveDataPresentationResponse,
        index: Long
    ) = when (liveDataResponse) {
        is LiveDataPresentationResponse.Loading -> Section(
            Section(SimpleMessageItem(stringProvider.loadingMessage(), liveDataResponse.serviceLocation.id()))
        )
        is LiveDataPresentationResponse.Skipped -> Section()
        is LiveDataPresentationResponse.Data -> {
            val liveData = liveDataResponse.liveData
            if (liveData.isNullOrEmpty()) {
                Section(
                    SimpleMessageItem(
                        stringProvider.noArrivalsMessage(liveDataResponse.serviceLocation.service),
                        liveDataResponse.serviceLocation.id()
                    )
                )
            } else if (liveData.size == 1 && liveData.first().size == 1 && liveData.first().first() is DockLiveData) {
                Section(
                    DublinBikesLiveDataItem(
                        liveData.first().first() as DockLiveData
                    )
                )
            } else {
                val items = mutableListOf<GroupedLiveDataItem>()
                for (thing in liveData.take(AppConstants.favouritesLiveDataLimit)) {
                    items.add(GroupedLiveDataItem(thing.take(AppConstants.favouritesGroupedLiveDataLimit) as List<PredictionLiveData>))
                }
                Section(items)
            }
        }
        is LiveDataPresentationResponse.Error -> {
            Timber.e(liveDataResponse.throwable, "Error getting favourites live data")
            Section(
                SimpleMessageItem(
                    stringProvider.serviceErrorMessage(
                        liveDataResponse.serviceLocation.service, liveDataResponse.throwable
                    ),
                    liveDataResponse.serviceLocation.id()
                )
            )
        }
    }

    private fun mapServiceLocation(
        locationResponses: ServiceLocationPresentationResponse
    ) = SimpleServiceLocationItem(
        serviceLocation = locationResponses.serviceLocation,
        walkDistance = locationResponses.distance
    )

    private fun mapDivider(
        items: Int,
        favourite: DubLinkServiceLocation,
        index: Int
    ) = if (index < items - 1) DividerItem(index.toLong()) else null
}
