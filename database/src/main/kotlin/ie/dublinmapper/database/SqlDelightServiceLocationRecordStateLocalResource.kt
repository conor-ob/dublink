package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.rtpi.api.Service
import java.time.Instant

class SqlDelightServiceLocationRecordStateLocalResource(
    private val database: Database
) : ServiceLocationRecordStateLocalResource {

    override fun selectRecordState(service: Service): Instant {
        return database.locationExpirationsQueries.selectById(service)
            .executeAsOne().lastUpdated
    }

    override fun insertRecordState(service: Service, lastUpdated: Instant) {
        database.locationExpirationsQueries.insertOrReplace(
            service = service,
            lastUpdated = lastUpdated
        )
    }
}
