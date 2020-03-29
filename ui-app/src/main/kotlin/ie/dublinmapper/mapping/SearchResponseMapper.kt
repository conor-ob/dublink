package ie.dublinmapper.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.SearchResponse
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.ui.R
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object SearchResponseMapper : CustomConverter<SearchResponse, Group>() {

    override fun convert(
        source: SearchResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ) = Section(
        source.serviceLocations.map {
            ServiceLocationItem(
                serviceLocation = it,
                icon = mapIcon(it.service),
                routes = mapRoutes(
                    it
                ),
                walkDistance = null
            )
        }
    )

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
