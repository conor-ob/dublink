package io.dublink.favourites

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import io.dublink.domain.internet.NetworkUnavailableException
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.id
import io.dublink.domain.service.StringProvider
import io.dublink.model.DividerItem
import io.dublink.model.DublinBikesLiveDataItem
import io.dublink.model.GroupedLiveDataItem
import io.dublink.model.SimpleMessageItem
import io.dublink.model.SimpleServiceLocationItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import timber.log.Timber

class FavouritesMapper @Inject constructor(
    private val stringProvider: StringProvider
) {

    fun map(
        favourites: List<DubLinkServiceLocation>?,
        liveData: List<LiveDataPresentationResponse>?
    ): Group {
        return if (favourites != null && favourites.isNotEmpty()) {
            Section(
                favourites.mapIndexed { index, dubLinkServiceLocation ->
                    Section(
                        listOfNotNull(
                            mapServiceLocation(
                                dubLinkServiceLocation
                            ),
                            mapLiveData(
                                dubLinkServiceLocation,
                                liveData,
                                index
                            ),
                            mapDivider(
                                favourites.size,
                                dubLinkServiceLocation,
                                index
                            )
                        )
                    )
                }
            )
        } else if (favourites != null && favourites.isEmpty()) {
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
            Section(SimpleMessageItem("Loading...", favourite.id()))
        } else {
            val match = liveData.find {
                it.serviceLocation.service == favourite.service &&
                it.serviceLocation.id == favourite.id
            }
            if (match == null) {
                Section(SimpleMessageItem("Loading...", favourite.id()))
            } else {
                mapLiveData(match, favourite.id())
            }
        }
    }

    private fun mapLiveData(
        liveDataResponse: LiveDataPresentationResponse,
        index: Long
    ) = when (liveDataResponse) {
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
                for (thing in liveData.take(3)) {
                    items.add(GroupedLiveDataItem(thing.take(3) as List<PredictionLiveData>))
                }
                Section(items)
            }
        }
        is LiveDataPresentationResponse.Error -> {
            Timber.e(liveDataResponse.throwable, "Error getting favourites live data")
            val message = when (liveDataResponse.throwable) {
                // service is down
                is ConnectException -> "${liveDataResponse.serviceLocation.service.fullName} service is down"

                // user has no internet connection
                is NetworkUnavailableException,
                is UnknownHostException -> "Please check your internet connection"

                // network error
                is IOException -> "We're having trouble reaching ${liveDataResponse.serviceLocation.service.fullName}"

                else -> "Something went wrong, try refreshing"
            }
            Section(
                SimpleMessageItem(message, liveDataResponse.serviceLocation.id())
            )
        }
    }

    private fun mapServiceLocation(
        favourite: DubLinkServiceLocation
    ) = SimpleServiceLocationItem(
        serviceLocation = favourite,
        walkDistance = null
    )

    private fun mapDivider(
        items: Int,
        favourite: DubLinkServiceLocation,
        index: Int
    ) = if (index < items - 1) DividerItem(favourite.id()) else null
}
