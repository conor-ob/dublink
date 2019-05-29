package ie.dublinmapper.view.livedata.dart

import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.view.BasePresenter
import ie.dublinmapper.view.livedata.LiveDataMapper
import timber.log.Timber
import javax.inject.Inject

class DartLiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    scheduler: RxScheduler
) : BasePresenter<DartLiveDataView>(scheduler), DartLiveDataPresenter {

    override fun start(stationId: String) {
        subscriptions().add(useCase.getLiveData(DartStation(id = stationId, name = "", coordinate = Coordinate(0.0, 0.0), operators = Operator.dart()))
            .compose(applySchedulers())
            .map { LiveDataMapper.map(it) }
            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    override fun stop() {
        unsubscribe()
    }

}
