package ie.dublinmapper.repository

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.store.rx2.observe
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import io.reactivex.Observable
import io.rtpi.api.LiveData

class ServiceLiveDataRepository<T : LiveData>(
    private val liveDataStore: Store<String, List<T>>
) : LiveDataRepository {

    override fun get(key: LiveDataKey): Observable<StoreResponse<List<LiveData>>> {
        // TODO decide when to use the cache
//        return liveDataStore.observe(key.locationId).toObservable().map { it }
        return liveDataStore
            .observe(
                StoreRequest.cached(
                    key = key.locationId,
                    refresh = false)
            )
            .toObservable()
            .map { it }
    }
}
