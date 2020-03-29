package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.LocationRepository
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.repository.ServiceLocationKey
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class LiveDataUseCase @Inject constructor(
    @Named("SERVICE_LOCATION") private val locationRepository: LocationRepository,
    private val aircoachLiveDataRepository: Repository<AircoachLiveData>,
    private val busEireannLiveDataRepository: Repository<BusEireannLiveData>,
    private val irishRailLiveDataRepository: Repository<IrishRailLiveData>,
    private val dublinBikesLiveDataRepository: Repository<DublinBikesLiveData>,
    private val dublinBusLiveDataRepository: Repository<DublinBusLiveData>,
    private val luasLiveDataRepository: Repository<LuasLiveData>
) {

    fun getServiceLocation(serviceLocationId: String, service: Service): Observable<ServiceLocation> {
        return locationRepository.get(ServiceLocationKey(service = service, locationId = serviceLocationId))
    }

    fun getLiveDataStream(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<LiveDataResponse> {
        return Observable.interval(0L, 65L, TimeUnit.SECONDS)
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
