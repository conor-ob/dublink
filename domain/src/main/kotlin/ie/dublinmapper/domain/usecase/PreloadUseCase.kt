package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.RxScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PreloadUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val dartStationRepository: Repository<DartStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val swordsExpressStopRepository: Repository<SwordsExpressStop>,
    private val scheduler: RxScheduler
) {

    private val subscriptions = CompositeDisposable()

    fun start() {
        subscriptions.addAll(
            aircoachStopRepository.getAll().subscribeOn(scheduler.io).subscribe(),
            busEireannStopRepository.getAll().subscribeOn(scheduler.io).subscribe(),
            dartStationRepository.getAll().subscribeOn(scheduler.io).subscribe(),
            dublinBikesDockRepository.getAll().subscribeOn(scheduler.io).subscribe(),
            dublinBusStopRepository.getAll().subscribeOn(scheduler.io).subscribe(),
            luasStopRepository.getAll().subscribeOn(scheduler.io).subscribe()
        )
    }

    fun stop() {
        subscriptions.clear()
        subscriptions.dispose()
    }

}