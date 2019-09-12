package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.room.RoomPersister
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.persister.PersisterEntity
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.TimeUtils
import io.reactivex.Maybe
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

abstract class AbstractPersister<Raw, Parsed, Key>(
    private val memoryPolicy: MemoryPolicy,
    private val persisterDao: PersisterDao,
    private val internetManager: InternetManager
) : RoomPersister<Raw, Parsed, Key>, RecordProvider<Key> {

    private val lifespan: Long by lazy { memoryPolicy.expireAfterTimeUnit.toSeconds(memoryPolicy.expireAfterWrite) }

    abstract fun select(key: Key): Maybe<Parsed>

    abstract fun insert(key: Key, raw: Raw)

    override fun read(key: Key): Observable<Parsed> {
        return select(key).toObservable()
    }

    override fun write(key: Key, raw: Raw) {
        insert(key, raw)
        persisterDao.insert(PersisterEntity(key.toString()))
    }

    override fun getRecordState(key: Key): RecordState {
        val persisterEntity = persisterDao.select(key.toString()).blockingGet()
        return getRecordStateFromTimestamp(persisterEntity)
    }

    private fun getRecordStateFromTimestamp(persisterEntity: PersisterEntity?): RecordState {
        if (persisterEntity == null) {
            return RecordState.STALE
        }
        val elapsedSeconds = TimeUtils.now().epochSecond - persisterEntity.lastUpdated.epochSecond
        if (elapsedSeconds > lifespan) {
            return RecordState.STALE
        }
        if (elapsedSeconds > TimeUnit.DAYS.toSeconds(1) && internetManager.isConnectedToWiFi()) {
            return RecordState.STALE
        }
        return RecordState.FRESH
    }

}
