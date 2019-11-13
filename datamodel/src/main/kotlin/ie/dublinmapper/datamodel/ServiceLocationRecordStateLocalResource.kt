package ie.dublinmapper.datamodel

import org.threeten.bp.Instant

interface ServiceLocationRecordStateLocalResource {

    fun selectRecordState(id: String): Instant

    fun insertRecordState(id: String, lastUpdated: Instant)

}
