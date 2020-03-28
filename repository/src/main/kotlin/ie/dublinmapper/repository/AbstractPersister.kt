package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.room.RoomPersister
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.domain.service.InternetManager
import io.reactivex.Observable
import java.time.Instant
import java.util.concurrent.TimeUnit

abstract class AbstractPersister<Raw, Parsed, Key>(
    private val memoryPolicy: MemoryPolicy,
    private val serviceLocationRecordStateLocalResource: ServiceLocationRecordStateLocalResource,
    private val internetManager: InternetManager
) : RoomPersister<Raw, Parsed, Key>, RecordProvider<Key> {

    private val lifespan: Long by lazy { memoryPolicy.expireAfterTimeUnit.toSeconds(memoryPolicy.expireAfterWrite) }

    abstract fun select(key: Key): Observable<Parsed>

    abstract fun insert(key: Key, raw: Raw)

    override fun read(key: Key): Observable<Parsed> {
        return select(key)
    }

    override fun write(key: Key, raw: Raw) {
        insert(key, raw)
        serviceLocationRecordStateLocalResource.insertRecordState(
            id = key.toString(),
            lastUpdated = Instant.now()
        )
    }

    override fun getRecordState(key: Key): RecordState {
        val lastUpdated = serviceLocationRecordStateLocalResource.selectRecordState(key.toString())
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
        if (elapsedSeconds > TimeUnit.DAYS.toSeconds(1) && internetManager.isConnectedToWiFi()) {
            return RecordState.STALE
        }
        return RecordState.FRESH
    }

}
