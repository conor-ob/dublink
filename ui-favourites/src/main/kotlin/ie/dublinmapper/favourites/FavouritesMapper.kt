package ie.dublinmapper.favourites

import com.xwray.groupie.Section
import ie.dublinmapper.domain.internet.NetworkUnavailableException
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.GroupedLiveDataItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.SimpleMessageItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.Service
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import timber.log.Timber

object FavouritesMapper {

    fun map(
        liveData: List<LiveDataPresentationResponse>
    ) = Section(
        liveData.mapIndexed { index: Int, response: LiveDataPresentationResponse ->
            Section(
                listOfNotNull(
                    mapServiceLocation(response.serviceLocation),
                    mapLiveData(response, index),
                    mapDivider(liveData.size, index)
                )
            )
        }
    )

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
                is IOException -> "⚠️ We're having trouble reaching ${liveDataResponse.serviceLocation.service.fullName}"

                else -> "Oops! Something went wrong"
            }
            Section(
                SimpleMessageItem(message, index)
            )
        }
    }

    private fun mapServiceLocation(
        serviceLocation: DubLinkServiceLocation
    ) = ServiceLocationItem(
        serviceLocation = serviceLocation,
        icon = mapIcon(serviceLocation.service),
//                        routes = if (liveData.isNullOrEmpty()) mapRoutes(serviceLocation) else null,
//                        routes = if (serviceLocation.service == Service.DUBLIN_BIKES) mapRoutes(serviceLocation, liveDataResponse.liveData) else null,
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

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem(index.toLong()) else null

    private fun mapIcon(service: Service): Int = when (service) {
        Service.AIRCOACH,
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> R.drawable.ic_bus
        Service.DUBLIN_BIKES -> R.drawable.ic_bike
        Service.IRISH_RAIL -> R.drawable.ic_train
        Service.LUAS -> R.drawable.ic_tram
    }
}
