package ie.dublinmapper.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.FavouritesResponse
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.domain.usecase.LiveDataResponse
import ie.dublinmapper.domain.usecase.State
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.LiveDataItem
import ie.dublinmapper.model.NoLiveDataItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.ui.R
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class FavouritesResponseMapper(
    private val stringProvider: StringProvider
) : CustomConverter<FavouritesResponse, Group>() {

    override fun convert(
        source: FavouritesResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ) = Section(
        source.serviceLocations.entries.mapIndexed { index, entry ->
            val serviceLocation = entry.key
            val liveDataResponse = entry.value
            Section(
                listOfNotNull(
                    ServiceLocationItem(
                        serviceLocation = serviceLocation,
                        icon = mapIcon(serviceLocation.service),
//                        routes = if (liveData.isNullOrEmpty()) mapRoutes(serviceLocation) else null,
                        routes = null,
                        walkDistance = null
                    ),
                    mapLiveData(serviceLocation.service, liveDataResponse),
                    if (index < source.serviceLocations.entries.size - 1) DividerItem() else null
                )
            )
        }
    )

    private fun mapLiveData(service: Service, liveDataResponse: LiveDataResponse): Section {
        if (liveDataResponse.state == State.LOADING) {
            return Section(NoLiveDataItem("Loading..."))
        } else {
            val items = liveDataResponse.liveData.mapNotNull {
                if (it is TimedLiveData) {
                    LiveDataItem(liveData = it)
                } else {
                    null // TODO
                }
            }
            return if (items.isNullOrEmpty()) {
                Section(NoLiveDataItem(mapMessage(service)))
            } else {
                Section(items)
            }
        }
    }

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

    private fun mapRoutes(serviceLocation: ServiceLocation): List<Route>? = when (serviceLocation) {
        is ServiceLocationRoutes -> serviceLocation.routes
        is DublinBikesDock -> listOf(
            Route("${serviceLocation.availableBikes} Bikes", Operator.DUBLIN_BIKES),
            Route("${serviceLocation.availableDocks} Docks", Operator.DUBLIN_BIKES)
        )
        else -> null
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
