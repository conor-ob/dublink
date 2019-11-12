package ie.dublinmapper.datamodel.persister

import org.threeten.bp.Instant

interface ServiceLocationRecordStateLocalResource {

    fun selectRecordState(id: String): Instant

    fun insertRecordState(id: String, lastUpdated: Instant)

}
