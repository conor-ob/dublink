package ie.dublinmapper.database

import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.rtpi.api.Service
import java.time.Instant

class SqlDelightServiceLocationRecordStateLocalResource(
    private val database: Database
) : ServiceLocationRecordStateLocalResource {

    override fun selectRecordState(service: Service): Instant {
        return database.locationExpirationQueries.selectById(service)
            .executeAsOne().lastUpdated //TODO check null
    }

    override fun insertRecordState(service: Service, lastUpdated: Instant) {
        database.locationExpirationQueries.insertOrReplace(
            service = service,
            lastUpdated = lastUpdated
        )
    }
}
