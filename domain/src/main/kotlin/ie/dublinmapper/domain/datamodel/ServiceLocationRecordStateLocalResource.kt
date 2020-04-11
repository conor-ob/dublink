package ie.dublinmapper.domain.datamodel

import io.rtpi.api.Service
import java.time.Instant

interface ServiceLocationRecordStateLocalResource {

    fun selectRecordState(service: Service): Instant

    fun insertRecordState(service: Service, lastUpdated: Instant)
}
