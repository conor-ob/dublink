package ie.dublinmapper.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.internet.NetworkUnavailableException
import ie.dublinmapper.domain.usecase.FavouritesResponse
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.domain.usecase.LiveDataResponse
import ie.dublinmapper.model.*
import ie.dublinmapper.ui.R
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import timber.log.Timber
import java.net.ConnectException

class FavouritesResponseMapper(
    private val stringProvider: StringProvider
) : CustomConverter<FavouritesResponse, Group>() {

    override fun convert(
        source: FavouritesResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ) = Section(
        source.liveDataResponses.mapIndexed { index, liveDataResponse ->
            Section(
                listOfNotNull(
                    mapServiceLocation(liveDataResponse),
                    mapLiveData(liveDataResponse),
                    mapDivider(source.liveDataResponses.size, index)
                )
            )
        }
    )

    private fun mapServiceLocation(
        liveDataResponse: LiveDataResponse
    ) = ServiceLocationItem(
        serviceLocation = liveDataResponse.serviceLocation,
        icon = mapIcon(liveDataResponse.serviceLocation.service),
//                        routes = if (liveData.isNullOrEmpty()) mapRoutes(serviceLocation) else null,
//                        routes = if (serviceLocation.service == Service.DUBLIN_BIKES) mapRoutes(serviceLocation, liveDataResponse.liveData) else null,
        routes = null,
        walkDistance = null
    )

    private fun mapLiveData(
        liveDataResponse: LiveDataResponse
    ) = when (liveDataResponse) {
        is LiveDataResponse.Loading -> Section(SimpleMessageItem("Loading..."))
        is LiveDataResponse.Skipped -> Section()
        is LiveDataResponse.Complete -> TODO()
        is LiveDataResponse.Grouped -> {
            val liveData = liveDataResponse.liveData
            if (liveData.isNullOrEmpty()) {
                Section(SimpleMessageItem(mapMessage(liveDataResponse.serviceLocation.service)))
            } else if (liveData.size == 1 && liveData.first().size == 1 && liveData.first().first() is DublinBikesLiveData) {
                Section(DublinBikesLiveDataItem(liveData.first().first() as DublinBikesLiveData))
            } else {
                val items = mutableListOf<GroupedLiveDataItem>()
                for (thing in liveData.take(3)) {
                    items.add(GroupedLiveDataItem(thing as List<TimedLiveData>))
                }
                Section(items)
            }
        }
        is LiveDataResponse.Error -> {
            Timber.e(liveDataResponse.throwable, "Error getting live data")
            val message = when (liveDataResponse.throwable) {
                is ConnectException -> "⚠️ We're having trouble reaching ${liveDataResponse.serviceLocation.service.fullName}"
                is NetworkUnavailableException  -> "Please check your internet connection"
                else -> "Oops! Something went wrong"
            }
            Section(
                SimpleMessageItem(message)
            )
        }
    }

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem() else null

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

    private fun mapIcon(service: Service): Int = when (service) {
        Service.AIRCOACH,
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> R.drawable.ic_bus
        Service.DUBLIN_BIKES -> R.drawable.ic_bike
        Service.IRISH_RAIL -> R.drawable.ic_train
        Service.LUAS -> R.drawable.ic_tram
    }
}
