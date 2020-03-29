package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import io.reactivex.Observable
import io.rtpi.api.LiveData

class ServiceLiveDataRepository<T : LiveData>(
    private val liveDataStore: Store<List<T>, String>
) : LiveDataRepository {

    override fun get(key: LiveDataKey): Observable<List<LiveData>> {
        // TODO decide when to use the cache
        return liveDataStore.get(key.locationId).toObservable().map { it }
    }
}
