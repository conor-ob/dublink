package io.dublink.repository.location

import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.room.RoomPersister
import io.dublink.domain.datamodel.ServiceLocationLocalResource
import io.dublink.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.InternetManager
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import java.time.Instant
import java.util.concurrent.TimeUnit

class ServiceLocationPersister(
    private val memoryPolicy: MemoryPolicy,
    private val serviceLocationLocalResource: ServiceLocationLocalResource,
    private val serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    private val internetManager: InternetManager
) : RoomPersister<List<ServiceLocation>, List<DubLinkServiceLocation>, Service>, RecordProvider<Service> {

    private val lifespan: Long by lazy { memoryPolicy.expireAfterTimeUnit.toSeconds(memoryPolicy.expireAfterWrite) }

    override fun read(key: Service): Observable<List<DubLinkServiceLocation>> {
        return serviceLocationLocalResource.selectServiceLocations(key)
    }

    override fun write(key: Service, raw: List<ServiceLocation>) {
        serviceLocationLocalResource.insertServiceLocations(key, raw)
        serviceLocationRecordStateLocalResource.insertRecordState(
            service = key,
            lastUpdated = Instant.now()
        )
    }

    override fun getRecordState(key: Service): RecordState {
        val lastUpdated = serviceLocationRecordStateLocalResource.selectRecordState(key)
        return getRecordStateFromTimestamp(lastUpdated)
    }

    private fun getRecordStateFromTimestamp(lastUpdated: Instant?): RecordState {
        if (lastUpdated == null) {
            return RecordState.STALE
        }
        val elapsedSeconds = Instant.now().epochSecond - lastUpdated.epochSecond
        if (elapsedSeconds > lifespan) {
            return RecordState.STALE
        }
        if (elapsedSeconds > TimeUnit.DAYS.toSeconds(1) && internetManager.isWiFiAvailable()) {
            return RecordState.STALE
        }
        return RecordState.FRESH
    }
}
