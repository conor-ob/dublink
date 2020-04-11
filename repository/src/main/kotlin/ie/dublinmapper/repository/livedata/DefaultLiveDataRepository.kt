package ie.dublinmapper.repository.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import io.reactivex.Observable
import io.rtpi.api.LiveData

class DefaultLiveDataRepository(
    private val liveDataStore: Store<List<LiveData>, LiveDataKey>
) : LiveDataRepository {

    override fun get(
        key: LiveDataKey
    ): Observable<LiveDataResponse> = liveDataStore
        .get(key)
        .toObservable()
        .map<LiveDataResponse> { liveData ->
            LiveDataResponse.Data(liveData)
        }
        .onErrorReturn { throwable ->
            LiveDataResponse.Error(throwable)
        }
}
