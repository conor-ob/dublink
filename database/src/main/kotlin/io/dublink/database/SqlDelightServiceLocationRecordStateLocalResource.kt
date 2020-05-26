package io.dublink.database

import io.dublink.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.rtpi.api.Service
import java.time.Instant

class SqlDelightServiceLocationRecordStateLocalResource(
    private val database: Database
) : ServiceLocationRecordStateLocalResource {

    override fun selectRecordState(service: Service): Instant? {
        return database.locationExpirationQueries
            .selectById(service)
            .executeAsOneOrNull()?.lastUpdated
    }

    override fun insertRecordState(service: Service, lastUpdated: Instant) {
        database.locationExpirationQueries.insertOrReplace(
            service = service,
            lastUpdated = lastUpdated
        )
    }
}
