package ie.dublinmapper.database.persister

import ie.dublinmapper.database.Database
import ie.dublinmapper.datamodel.persister.ServiceLocationRecordStateLocalResource
import org.threeten.bp.Instant

class SqlDelightServiceLocationRecordStateLocalResource(
    private val database: Database
) : ServiceLocationRecordStateLocalResource {

    override fun selectRecordState(id: String): Instant {
        return database.serviceLocationRecordStateEntityQueries.selectById(id)
            .executeAsOne().lastUpdated
    }

    override fun insertRecordState(id: String, lastUpdated: Instant) {
        database.serviceLocationRecordStateEntityQueries.insertOrReplace(
            id = id,
            lastUpdated = lastUpdated
        )
    }

}
