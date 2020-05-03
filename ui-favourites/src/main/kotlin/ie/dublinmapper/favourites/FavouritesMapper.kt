package ie.dublinmapper.favourites

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.internet.NetworkUnavailableException
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.id
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.GroupedLiveDataItem
import ie.dublinmapper.model.SimpleMessageItem
import ie.dublinmapper.model.SimpleServiceLocationItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.Service
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import timber.log.Timber

object FavouritesMapper {

    fun map(
        favourites: List<DubLinkServiceLocation>?,
        liveData: List<LiveDataPresentationResponse>?
    ): Group {
        return if (favourites != null) {
            Section(
                favourites.mapIndexed { index, dubLinkServiceLocation ->
                    Section(
                        listOfNotNull(
                            mapServiceLocation(dubLinkServiceLocation),
                            mapLiveData(dubLinkServiceLocation, liveData, index),
                            mapDivider(favourites.size, dubLinkServiceLocation, index)
                        )
                    )
                }
            )
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
            Timber.d("TEST 1")
            Section(SimpleMessageItem("Loading...", favourite.id()))
        } else {
            val match = liveData.find {
                it.serviceLocation.service == favourite.service &&
                it.serviceLocation.id == favourite.id
            }
            if (match == null) {
                Timber.d("TEST 2")
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
                Section(SimpleMessageItem(mapMessage(liveDataResponse.serviceLocation.service), liveDataResponse.serviceLocation.id()))
            } else if (liveData.size == 1 && liveData.first().size == 1 && liveData.first().first() is DockLiveData) {
                Section(DublinBikesLiveDataItem(liveData.first().first() as DockLiveData))
            } else {
                val items = mutableListOf<GroupedLiveDataItem>()
                for (thing in liveData.take(3)) {
                    items.add(GroupedLiveDataItem(thing.take(3) as List<PredictionLiveData>))
                }
                Section(items)
            }
        }
        is LiveDataPresentationResponse.Error -> {
            Timber.e(liveDataResponse.throwable, "Error getting live data")
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

    private fun mapMessage(service: Service): String {
        val mode = when (service) {
            Service.AIRCOACH,
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS -> "buses"
            Service.IRISH_RAIL -> "trains"
            Service.LUAS -> "trams"
            else -> "arrivals"
        }
        return "No scheduled $mode"
    }

    private fun mapDivider(
        items: Int,
        favourite: DubLinkServiceLocation,
        index: Int
    ) = if (index < items - 1) DividerItem(favourite.id()) else null
}
