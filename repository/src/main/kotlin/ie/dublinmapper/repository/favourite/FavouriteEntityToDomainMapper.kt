package ie.dublinmapper.repository.favourite

import ie.dublinmapper.data.favourite.FavouriteEntity
import ie.dublinmapper.data.favourite.FavouriteServiceEntity
import ie.dublinmapper.domain.model.Favourite
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object FavouriteEntityToDomainMapper : CustomConverter<FavouriteEntity, Favourite>() {

    override fun convert(
        source: FavouriteEntity,
        destinationType: Type<out Favourite>,
        mappingContext: MappingContext
    ): Favourite {
        return Favourite(
            id = source.location.id,
            name = source.location.name,
            service = source.location.service,
            routes = mapRoutes(source.services)
        )
    }

    private fun mapRoutes(services: List<FavouriteServiceEntity>): Map<Operator, List<String>> {
        val operatorsByRoute = mutableMapOf<Operator, List<String>>()
        for (entity in services) {
            val operator = entity.operator
            val routes = operatorsByRoute[operator]
            if (routes == null) {
                operatorsByRoute[operator] = listOf(entity.route)
            } else {
                val newRoutes = routes.toMutableList()
                newRoutes.add(entity.route)
//                newRoutes.sortedWith(AlphanumComp)
                operatorsByRoute[operator] = newRoutes
            }
        }
        return operatorsByRoute
    }

}
