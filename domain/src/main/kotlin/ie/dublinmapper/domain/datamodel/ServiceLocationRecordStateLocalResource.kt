package ie.dublinmapper.domain.datamodel

import java.time.Instant

interface ServiceLocationRecordStateLocalResource {

    fun selectRecordState(id: String): Instant

    fun insertRecordState(id: String, lastUpdated: Instant)
}
