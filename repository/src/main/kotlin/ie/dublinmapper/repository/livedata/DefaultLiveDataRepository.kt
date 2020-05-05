package ie.dublinmapper.repository.livedata

import com.nytimes.android.external.store3.base.impl.Store
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Operator
import io.rtpi.api.Prediction
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.RouteInfo
import io.rtpi.api.Service
import java.time.Duration
import java.time.ZonedDateTime

class DefaultLiveDataRepository(
    private val liveDataStore: Store<List<LiveData>, LiveDataKey>
) : LiveDataRepository {

    override fun get(
        key: LiveDataKey,
        refresh: Boolean
    ): Observable<LiveDataResponse> =
        if (refresh) {
            liveDataStore.fetch(key)
        } else {
            liveDataStore.get(key)
        }
            .toObservable()
            .map<LiveDataResponse> { liveData ->
                LiveDataResponse.Data(liveData)
            }
            .onErrorReturn { throwable ->
                LiveDataResponse.Error(throwable)
            }

//    override fun get(
//        key: LiveDataKey,
//        refresh: Boolean
//    ): Observable<LiveDataResponse> = Observable.just(
//        LiveDataResponse.Data(
//            liveData = listOf(
//                newLiveData(
//                    operator = Operator.DART,
//                    destination = "Howth",
//                    direction = "Northbound",
//                    waitTime = 0
//                ),
//                newLiveData(
//                    operator = Operator.COMMUTER,
//                    destination = "Dundalk",
//                    direction = "Northbound",
//                    waitTime = 6
//                ),
//                newLiveData(
//                    operator = Operator.DART,
//                    destination = "Bray",
//                    direction = "Southbound",
//                    waitTime = 13
//                ),
//                newLiveData(
//                    operator = Operator.DART,
//                    destination = "Greystones",
//                    direction = "Southbound",
//                    waitTime = 17
//                ),
//                newLiveData(
//                    operator = Operator.INTERCITY,
//                    destination = "Belfasht",
//                    direction = "Northbound",
//                    waitTime = 29
//                ),
//                newLiveData(
//                    operator = Operator.COMMUTER,
//                    destination = "Grand Canal Dock",
//                    direction = "Southbound",
//                    waitTime = 44
//                )
//            )
//        )
//    )
//
//    private fun newLiveData(
//        operator: Operator,
//        destination: String,
//        direction: String,
//        waitTime: Int
//    ) = PredictionLiveData(
//        service = Service.IRISH_RAIL,
//        operator = operator,
//        routeInfo = RouteInfo(
//            route = operator.fullName,
//            direction = direction,
//            origin = "",
//            destination = destination
//        ),
//        prediction = Prediction(
//            waitTime = Duration.ofMinutes(waitTime.toLong()),
//            currentDateTime = ZonedDateTime.now(),
//            scheduledDateTime = ZonedDateTime.now().plusMinutes(waitTime.toLong()),
//            expectedDateTime = ZonedDateTime.now().plusMinutes(waitTime.toLong())
//        )
//    )
}
