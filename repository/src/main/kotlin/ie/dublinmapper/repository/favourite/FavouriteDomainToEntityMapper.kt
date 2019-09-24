package ie.dublinmapper.repository.favourite

import ie.dublinmapper.datamodel.favourite.FavouriteEntity
import ie.dublinmapper.datamodel.favourite.FavouriteKey
import ie.dublinmapper.datamodel.favourite.FavouriteLocationEntity
import ie.dublinmapper.datamodel.favourite.FavouriteServiceEntity
import ie.dublinmapper.domain.model.Favourite
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object FavouriteDomainToEntityMapper : CustomConverter<Favourite, FavouriteEntity>() {

    override fun convert(
        source: Favourite,
        destinationType: Type<out FavouriteEntity>,
        mappingContext: MappingContext
    ): FavouriteEntity {
        val locationEntity = FavouriteLocationEntity(
            id = FavouriteKey(source.id, source.service),
            service = source.service,
            name = source.name,
            order = source.order
        )
        val serviceEntities = mutableListOf<FavouriteServiceEntity>()
        for (entry in source.routes) {
            for (route in entry.value) {
                serviceEntities.add(
                    FavouriteServiceEntity(
                        locationId = FavouriteKey(source.id, source.service),
                        operator = entry.key,
                        route = route
                    )
                )
            }
        }
        val entity = FavouriteEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
