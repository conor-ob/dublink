package ie.dublinmapper.view.livedata

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.model.*
import ie.dublinmapper.model.dart.DartLiveDataItem
import ie.dublinmapper.model.luas.LuasLiveDataItem
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.Service
import ie.dublinmapper.view.BasePresenter
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class LiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    private val mapper: MapperFacade,
    scheduler: RxScheduler
) : BasePresenter<LiveDataView>(scheduler), LiveDataPresenter {

    override fun start(serviceLocationId: String, service: Service) {
        subscriptions().add(useCase.getCondensedLiveData(serviceLocationId, service)
            .compose(applySchedulers())
            .map { mapLiveData(service, it) }
//            .map { mapper.mapAsList(it, Group::class.java) }
            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
            .doOnError { Timber.e(it) }
            .subscribe( {Timber.d(it.toString())} , {Timber.e(it)} )
        )
    }

    override fun stop() {
        unsubscribe()
    }

    private fun mapLiveData(service: Service, liveData: List<LiveData>): List<Group> {
        return when (service) {
            Service.AIRCOACH -> mapAircoachLiveData(liveData)
            Service.IRISH_RAIL -> mapDartLiveData(liveData)
            Service.DUBLIN_BUS -> mapDublinBusLiveData(liveData)
            Service.LUAS -> mapLuasLiveData(liveData)
            else -> TODO()
        }
    }

    private fun mapAircoachLiveData(liveData: List<LiveData>): List<Group> {
        val liveDataUi = LiveDataMapper.map(liveData).map { it as AircoachLiveDataUi }
        val groups = liveDataUi.groupBy { it.liveData.destination }

        return emptyList()
    }

    private fun mapLuasLiveData(liveData: List<LiveData>): List<Group> {
        val liveDataUi = LiveDataMapper.map(liveData).map { it as LuasLiveDataUi }
        val groups = liveDataUi.groupBy { it.liveData.direction }

        val items = mutableListOf<Group>()
        items.add(DividerItem())
        for (entry in groups.entries) {
            val section = Section(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                val isLast = i == values.size - 1
                val isEven = i % 2 == 0
                section.add(LuasLiveDataItem(values[i]))
            }
            items.add(section)
            items.add(DividerItem())
        }
        return items
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
            val section = Section(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                val isLast = i == values.size - 1
                val isEven = i % 2 == 0
                section.add(DartLiveDataItem(values[i], isEven, isLast))
            }
            items.add(section)
            items.add(DividerItem())
        }
        return items
    }

}
