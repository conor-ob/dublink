package ie.dublinmapper.repository.favourite

import ie.dublinmapper.data.favourite.FavouriteEntity
import ie.dublinmapper.data.favourite.FavouriteLocationEntity
import ie.dublinmapper.data.favourite.FavouriteServiceEntity
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
            id = source.id,
            name = source.name,
            order = 0, //TODO
            service = source.service
        )
        val serviceEntities = mutableListOf<FavouriteServiceEntity>()
        for (entry in source.routes) {
            for (route in entry.value) {
                serviceEntities.add(
                    FavouriteServiceEntity(
                        locationId = source.id,
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
