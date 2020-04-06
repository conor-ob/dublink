package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import io.reactivex.Observable
import io.rtpi.api.LiveData

class DefaultLiveDataRepository<T : LiveData>(
    private val liveDataStore: Store<List<T>, String>
) : LiveDataRepository {

    override fun get(
        key: LiveDataKey
    ): Observable<LiveDataResponse> = liveDataStore
        .get(key.locationId)
        .toObservable()
        .map<LiveDataResponse> { liveData ->
            LiveDataResponse.Data(liveData)
        }
        .onErrorReturn { throwable ->
            LiveDataResponse.Error(throwable)
        }
}
