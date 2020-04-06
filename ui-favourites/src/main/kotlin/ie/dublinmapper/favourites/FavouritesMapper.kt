package ie.dublinmapper.favourites

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.internet.NetworkUnavailableException
import ie.dublinmapper.domain.repository.LiveDataResponse
import ie.dublinmapper.model.*
import io.rtpi.api.DublinBikesLiveData
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.TimedLiveData
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

object FavouritesMapper {

    fun map(
        favourites: FavouritesPresentationResponse?,
        liveData: List<LiveDataPresentationResponse>?
    ): Group {
        return if (favourites == null && liveData == null) {
            Section()
        } else if (favourites == null && liveData != null) {
            Section() // TODO shouldn't happen
        } else if (favourites != null && liveData == null) {
            Section(
                favourites.favourites.mapIndexed { index: Int, serviceLocation: ServiceLocation ->
                    Section(
                        listOfNotNull(
                            mapServiceLocation(serviceLocation),
                            mapDivider(favourites.favourites.size, index)
                        )
                    )
                }
            )
        } else {
            Section(
                favourites!!.favourites.mapIndexed { index: Int, serviceLocation: ServiceLocation ->
                    Section(
                        listOfNotNull(
                            mapServiceLocation(serviceLocation),
                            mapLiveData(liveData!![0], index),
                            mapDivider(favourites!!.favourites.size, index)
                        )
                    )
                }
            )
        }
    }

    private fun mapLiveData(
        liveDataResponse: LiveDataPresentationResponse,
        index: Int
    ) = when (liveDataResponse) {
        is LiveDataPresentationResponse.Loading -> Section(SimpleMessageItem("Loading...", index))
        is LiveDataPresentationResponse.Skipped -> Section()
        is LiveDataPresentationResponse.Data -> {
            val liveData = liveDataResponse.liveData
            if (liveData.isNullOrEmpty()) {
                Section(SimpleMessageItem(mapMessage(liveDataResponse.serviceLocation.service), index))
            } else if (liveData.size == 1 && liveData.first().size == 1 && liveData.first().first() is DublinBikesLiveData) {
                Section(DublinBikesLiveDataItem(liveData.first().first() as DublinBikesLiveData))
            } else {
                val items = mutableListOf<GroupedLiveDataItem>()
                for (thing in liveData.take(3)) {
                    items.add(GroupedLiveDataItem(thing.take(3) as List<TimedLiveData>))
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
                is IOException -> "⚠️ We're having trouble reaching ${liveDataResponse.serviceLocation.service.fullName}"

                else -> "Oops! Something went wrong"
            }
            Section(
                SimpleMessageItem(message, index)
            )
        }
    }

    private fun mapServiceLocation(
        serviceLocation: ServiceLocation
    ) = ServiceLocationItem(
        serviceLocation = serviceLocation,
        icon = mapIcon(serviceLocation.service),
//                        routes = if (liveData.isNullOrEmpty()) mapRoutes(serviceLocation) else null,
//                        routes = if (serviceLocation.service == Service.DUBLIN_BIKES) mapRoutes(serviceLocation, liveDataResponse.liveData) else null,
        routes = null,
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

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem(index) else null

    private fun mapIcon(service: Service): Int = when (service) {
        Service.AIRCOACH,
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> R.drawable.ic_bus
        Service.DUBLIN_BIKES -> R.drawable.ic_bike
        Service.IRISH_RAIL -> R.drawable.ic_train
        Service.LUAS -> R.drawable.ic_tram
    }
}
