package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import io.rtpi.api.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LiveDataUseCase @Inject constructor(
    private val aircoachLiveDataRepository: Repository<AircoachLiveData>,
    private val busEireannLiveDataRepository: Repository<BusEireannLiveData>,
    private val irishRailLiveDataRepository: Repository<IrishRailLiveData>,
    private val dublinBikesLiveDataRepository: Repository<DublinBikesLiveData>,
    private val dublinBusLiveDataRepository: Repository<DublinBusLiveData>,
    private val luasLiveDataRepository: Repository<LuasLiveData>
) {

//    fun getCondensedLiveDataStream(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<LiveDataResponse> {
//        return Observable.interval(0L, 65L, TimeUnit.SECONDS)
//            .flatMap {
//                getLiveData(serviceLocationId, service).map {
//                    LiveDataResponse(service, serviceLocationName, condenseLiveData(it))
//                }
//            }
//    }

    fun getLiveDataStream(serviceLocationId: String, serviceLocationName: String, service: Service): Observable<LiveDataResponse> {
        return Observable.interval(0L, 65L, TimeUnit.SECONDS)
            .flatMap {
                getLiveData(serviceLocationId, service).map {
                    LiveDataResponse(service, serviceLocationName, it)
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

//    private fun condenseLiveData(liveData: List<LiveData>): List<LiveData> {
//        val condensedLiveData = LinkedHashMap<Int, LiveData>()
//        for (data in liveData) {
//            var cachedLiveData = condensedLiveData[data.customHash]
//            if (cachedLiveData == null) {
//                condensedLiveData[data.customHash] = data
//            } else {
//                when (cachedLiveData) {
//                    is AircoachLiveData<*> -> {
//                        val aircoachLiveData = data as AircoachLiveData<*>
//                        val dueTimes = cachedLiveData.dueTime.toMutableList()
//                        dueTimes.add(aircoachLiveData.dueTime[0])
//                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
//                    }
//                    is BusEireannLiveData<*> -> {
//                        val busEireannLiveData = data as BusEireannLiveData
//                        val dueTimes = cachedLiveData.dueTime.toMutableList()
//                        dueTimes.add(busEireannLiveData.dueTime[0])
//                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
//                    }
//                    is IrishRailLiveData<*> -> {
//                        val irishRailLiveData = data as IrishRailLiveData
//                        val dueTimes = cachedLiveData.dueTime.toMutableList()
//                        dueTimes.add(irishRailLiveData.dueTime[0])
//                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
//                    }
//                    is DublinBusLiveData<*> -> {
//                        val dublinBusLiveData = data as DublinBusLiveData
//                        val dueTimes = cachedLiveData.dueTime.toMutableList()
//                        dueTimes.add(dublinBusLiveData.dueTime[0])
//                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
//                    }
//                    is LuasLiveData<*> -> {
//                        val luasLiveData = data as LuasLiveData
//                        val dueTimes = cachedLiveData.dueTime.toMutableList()
//                        dueTimes.add(luasLiveData.dueTime[0])
//                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
//                    }
//                }
//                condensedLiveData[data.customHash] = cachedLiveData
//            }
//        }
//        return condensedLiveData.values.toList()
//    }

}

data class LiveDataResponse(
    val service: Service,
    val serviceLocationName: String,
    val liveData: List<LiveData>
)
