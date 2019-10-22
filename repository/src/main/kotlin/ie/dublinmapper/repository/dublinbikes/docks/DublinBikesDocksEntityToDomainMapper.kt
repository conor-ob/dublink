package ie.dublinmapper.repository.dublinbikes.docks

import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockEntity
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockServiceEntity
import ie.dublinmapper.domain.model.DetailedDublinBikesDock
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object DublinBikesDocksEntityToDomainMapper : CustomConverter<DublinBikesDockEntity, DetailedDublinBikesDock>() {

    override fun convert(
        source: DublinBikesDockEntity,
        destinationType: Type<out DetailedDublinBikesDock>,
        mappingContext: MappingContext
    ): DetailedDublinBikesDock {
        return DetailedDublinBikesDock(
            DublinBikesDock(
                id = source.location.id,
                name = source.location.name,
                coordinate = Coordinate(source.location.latitude, source.location.longitude),
                operators = mapOperators(source.services),
                docks = source.services[0].docks,
                availableBikes = source.services[0].availableBikes,
                availableDocks = source.services[0].availableDocks
            )
        )
    }

    private fun mapOperators(entities: List<DublinBikesDockServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

}
