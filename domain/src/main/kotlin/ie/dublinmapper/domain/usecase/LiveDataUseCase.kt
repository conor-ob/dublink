package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class LiveDataUseCase @Inject constructor(
    private val aircoachLiveDataRepository: Repository<AircoachLiveData>,
    private val dartLiveDataRepository: Repository<DartLiveData>,
    private val dublinBikesLiveDataRepository: Repository<DublinBikesLiveData>,
    private val dublinBusLiveDataRepository: Repository<DublinBusLiveData>,
    private val luasLiveDataRepository: Repository<LuasLiveData>
) {

    fun getLiveData(serviceLocation: ServiceLocation): Observable<List<LiveData>> {
        return when (serviceLocation) {
            is AircoachStop -> aircoachLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is DartStation -> dartLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is DublinBikesDock -> dublinBikesLiveDataRepository.getById(serviceLocation.id).map { Collections.singletonList(it) as List<LiveData> }
            is DublinBusStop -> dublinBusLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is LuasStop -> luasLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is SwordsExpressStop -> throw UnsupportedOperationException()
        }
    }

    fun getCondensedLiveData(serviceLocation: ServiceLocation): Observable<List<LiveData>> {
        if (serviceLocation is DublinBikesDock) {
            return getLiveData(serviceLocation)
        }
        return getLiveData(serviceLocation).map { condenseLiveData(it) }
    }

    private fun condenseLiveData(liveData: List<LiveData>): List<LiveData> {
        val condensedLiveData = LinkedHashMap<Int, LiveData>()
        for (data in liveData) {
            var cachedLiveData = condensedLiveData[data.customHash]
            if (cachedLiveData == null) {
                condensedLiveData[data.customHash] = data
            } else {
                when (cachedLiveData) {
                    is AircoachLiveData -> {
                        val aircoachLiveData = data as AircoachLiveData
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(aircoachLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                    is DartLiveData -> {
                        val dartLiveData = data as DartLiveData
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(dartLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                    is DublinBusLiveData -> {
                        val dublinBusLiveData = data as DublinBusLiveData
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(dublinBusLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                    is LuasLiveData -> {
                        val luasLiveData = data as LuasLiveData
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(luasLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                }
                condensedLiveData[data.customHash] = cachedLiveData
            }
        }
        return condensedLiveData.values.toList().take(3)
    }

}
