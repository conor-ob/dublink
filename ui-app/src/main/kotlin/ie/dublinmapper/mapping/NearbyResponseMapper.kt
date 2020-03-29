package ie.dublinmapper.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.NearbyResponse
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.ui.R
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class NearbyResponseMapper(
    private val stringProvider: StringProvider
) : CustomConverter<NearbyResponse, Group>() {

    override fun convert(
        source: NearbyResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ) = Section(
        source.serviceLocations.entries.map {
            val walkDistance = it.key
            val serviceLocation = it.value
            ServiceLocationItem(
                serviceLocation = serviceLocation,
                icon = mapIcon(serviceLocation.service),
                routes = mapRoutes(serviceLocation),
                walkDistance = walkDistance
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
