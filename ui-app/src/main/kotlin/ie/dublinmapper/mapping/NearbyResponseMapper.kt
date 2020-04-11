//package ie.dublinmapper.mapping
//
//import com.xwray.groupie.Group
//import com.xwray.groupie.Section
//import com.xwray.groupie.kotlinandroidextensions.Item
//import ie.dublinmapper.domain.usecase.NearbyResponse
//import ie.dublinmapper.domain.service.StringProvider
//import ie.dublinmapper.model.ServiceLocationItem
//import ie.dublinmapper.ui.R
//import io.rtpi.api.*
//import ma.glasnost.orika.CustomConverter
//import ma.glasnost.orika.MappingContext
//import ma.glasnost.orika.metadata.Type
//
//object NearbyResponseMapper {
//
//    fun mapNearbyResponse(
//        source: NearbyResponse
//    ): List<Item> = source.serviceLocations.entries.flatMap {
//        val walkDistance = it.key
//        when (val serviceLocation = it.value) {
//            is ServiceLocationRoutes -> listOf(
//                ServiceLocationItem(
//                    serviceLocation = serviceLocation,
//                    icon = mapIcon(serviceLocation.service),
//                    routes = serviceLocation.routes,
//                    walkDistance = walkDistance
//                )
//            )
//            is DublinBikesDock -> listOf(
//                ServiceLocationItem(
//                    serviceLocation = serviceLocation,
//                    icon = mapIcon(serviceLocation.service),
//                    routes = emptyList(),
//                    walkDistance = walkDistance
//                )
//            )
//            else -> emptyList()
//        }
//    }
//
//    private fun mapIcon(service: Service): Int = when (service) {
//        Service.AIRCOACH,
//        Service.BUS_EIREANN,
//        Service.DUBLIN_BUS -> R.drawable.ic_bus
//        Service.DUBLIN_BIKES -> R.drawable.ic_bike
//        Service.IRISH_RAIL -> R.drawable.ic_train
//        Service.LUAS -> R.drawable.ic_tram
//    }
//}
