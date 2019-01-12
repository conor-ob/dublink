package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.util.Thread
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
            is DublinBikesDock -> dublinBikesLiveDataRepository.getById(serviceLocation.id).map { Collections.singletonList(it) as List<LiveData> }
            is DublinBusStop -> dublinBusLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
            is LuasStop -> luasLiveDataRepository.getAllById(serviceLocation.id).map { it as List<LiveData> }
        }
    }

}
