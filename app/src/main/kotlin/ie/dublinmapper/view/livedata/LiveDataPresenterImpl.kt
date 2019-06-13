package ie.dublinmapper.view.livedata

import com.xwray.groupie.Group
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.model.*
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
        subscriptions().add(useCase.getCondensedLiveData(serviceLocationId, service)
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
        return when (service) {
            Service.IRISH_RAIL -> mapDartLiveData(liveData)
            Service.DUBLIN_BUS -> mapDublinBusLiveData(liveData)
            else -> TODO()
        }
    }

    private fun mapDublinBusLiveData(liveData: List<LiveData>): List<Group> {
        val liveDataUi = LiveDataMapper.map(liveData).map { it as DublinBusLiveDataUi }

        val items = mutableListOf<Group>()
        items.add(DividerItem())
        items.add(HeaderItem("Departures"))
        for (i in 0 until liveDataUi.size) {
            if (i == liveDataUi.size - 1) {
                items.add(liveDataUi[i].toItem(true))
            } else {
                items.add(liveDataUi[i].toItem())
            }
        }
        items.add(DividerItem())
        return items
    }

    private fun mapDartLiveData(liveData: List<LiveData>): List<Group> {
        val liveDataUi = LiveDataMapper.map(liveData).map { it as DartLiveDataUi }
        val groups = liveDataUi.groupBy { it.liveData.direction }

        val items = mutableListOf<Group>()
        items.add(DividerItem())
        for (entry in groups.entries) {
            items.add(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                if (i == values.size - 1) {
                    items.add(values[i].toItem(true))
                } else {
                    items.add(values[i].toItem())
                }
            }
            items.add(DividerItem())
        }
        return items
    }

}
