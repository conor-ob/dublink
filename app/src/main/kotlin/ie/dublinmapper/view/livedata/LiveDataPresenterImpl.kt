package ie.dublinmapper.view.livedata

import com.xwray.groupie.Group
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.model.DartLiveDataUi
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.SpacerItem
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.Service
import ie.dublinmapper.view.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class LiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    scheduler: RxScheduler
) : BasePresenter<LiveDataView>(scheduler), LiveDataPresenter {

    override fun start(serviceLocationId: String, service: Service) {
        subscriptions().add(useCase.getLiveData(serviceLocationId, service)
            .compose(applySchedulers())
            .map { mapLiveData(service, it) }
            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    override fun stop() {
        unsubscribe()
    }

    private fun mapLiveData(service: Service, liveData: List<LiveData>): List<Group> {
        when (service) {
            Service.IRISH_RAIL -> return mapDartLiveData(liveData)
            else -> TODO()
        }
    }

    private fun mapDartLiveData(liveData: List<LiveData>): List<Group> {
        val liveDataUi = LiveDataMapper.map(liveData).map { it as DartLiveDataUi }
        val groups = liveDataUi.groupBy { it.liveData.direction }

        val items = mutableListOf<Group>()
        for (entry in groups.entries) {
            items.add(DividerItem())
            items.add(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                if (i == values.size - 1) {
                    items.add(values[i].toItem(true))
                } else {
                    items.add(values[i].toItem())
                }
            }
        }
        items.add(SpacerItem())
        return items
    }

}
