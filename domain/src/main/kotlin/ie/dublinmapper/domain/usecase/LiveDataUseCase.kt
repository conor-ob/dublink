package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LiveDataUseCase @Inject constructor(
    private val aircoachStopRepository: Repository<AircoachStop>,
    private val busEireannStopRepository: Repository<BusEireannStop>,
    private val irishRailStationRepository: Repository<IrishRailStation>,
    private val dublinBikesDockRepository: Repository<DublinBikesDock>,
    private val dublinBusStopRepository: Repository<DublinBusStop>,
    private val luasStopRepository: Repository<LuasStop>,
    private val aircoachLiveDataRepository: Repository<AircoachLiveData>,
    private val busEireannLiveDataRepository: Repository<BusEireannLiveData>,
    private val irishRailLiveDataRepository: Repository<IrishRailLiveData>,
    private val dublinBikesLiveDataRepository: Repository<DublinBikesLiveData>,
    private val dublinBusLiveDataRepository: Repository<DublinBusLiveData>,
    private val luasLiveDataRepository: Repository<LuasLiveData>
) {

    fun getServiceLocation(serviceLocationId: String, service: Service): Observable<ServiceLocation> {
        return when (service) {
            Service.AIRCOACH -> aircoachStopRepository.getById(serviceLocationId)
            Service.BUS_EIREANN -> busEireannStopRepository.getById(serviceLocationId)
            Service.IRISH_RAIL -> irishRailStationRepository.getById(serviceLocationId)
            Service.DUBLIN_BIKES -> dublinBikesDockRepository.getById(serviceLocationId)
            Service.DUBLIN_BUS -> dublinBusStopRepository.getById(serviceLocationId)
            Service.LUAS -> luasStopRepository.getById(serviceLocationId)
        }.map { it }
    }

    fun getLiveDataStream(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<LiveDataResponse> {
        return Observable.interval(0L, 60L, TimeUnit.SECONDS)
            .flatMap {
                getLiveData(serviceLocationId, service).map {
                    LiveDataResponse(service, serviceLocationName, it, State.COMPLETE)
                }
            }
    }

    private fun getLiveData(serviceLocationId: String, service: Service): Observable<List<LiveData>> {
        return when (service) {
            Service.AIRCOACH -> aircoachLiveDataRepository.getAllById(serviceLocationId)
            Service.BUS_EIREANN -> busEireannLiveDataRepository.getAllById(serviceLocationId)
            Service.IRISH_RAIL -> irishRailLiveDataRepository.getAllById(serviceLocationId)
            Service.DUBLIN_BIKES -> dublinBikesLiveDataRepository.getById(serviceLocationId).map { listOf(it) }
            Service.DUBLIN_BUS -> dublinBusLiveDataRepository.getAllById(serviceLocationId)
            Service.LUAS -> luasLiveDataRepository.getAllById(serviceLocationId)
        }.map { it as List<LiveData> }
    }

}

data class LiveDataResponse(
    val service: Service,
    val serviceLocationName: String,
    val liveData: List<LiveData>,
    val state: State
)

enum class State {
    LOADING, COMPLETE
}
