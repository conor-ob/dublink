package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.CollectionUtils
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class LiveDataUseCase @Inject constructor(
    private val dartLiveDataRepository: Repository<LiveData.Dart>,
    private val dublinBikesLiveDataRepository: Repository<LiveData.DublinBikes>,
    private val dublinBusLiveDataRepository: Repository<LiveData.DublinBus>,
    private val luasLiveDataRepository: Repository<LiveData.Luas>
) {

    fun getLiveData(serviceLocation: ServiceLocation): Observable<List<LiveData>> {
        return when (serviceLocation) {
            is DartStation -> dartLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is DublinBikesDock -> dublinBikesLiveDataRepository.getById(serviceLocation.id).map {
                Collections.singletonList(
                    it
                ) as List<LiveData>
            }
            is DublinBusStop -> dublinBusLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is LuasStop -> luasLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
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
                    is LiveData.Dart -> {
                        val dartLiveData = data as LiveData.Dart
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(dartLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                    is LiveData.DublinBus -> {
                        val dublinBusLiveData = data as LiveData.DublinBus
                        val dueTimes = cachedLiveData.dueTime.toMutableList()
                        dueTimes.add(dublinBusLiveData.dueTime[0])
                        cachedLiveData = cachedLiveData.copy(dueTime = dueTimes)
                    }
                    is LiveData.Luas -> {
                        val luasLiveData = data as LiveData.Luas
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
