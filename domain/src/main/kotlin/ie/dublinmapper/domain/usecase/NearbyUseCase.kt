package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import io.reactivex.Observable
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val dublinBikesDockRepository: Repository<DublinBikesDock>
) {

    fun getNearbyServiceLocations(): Observable<List<DublinBikesDock>> {
        return dublinBikesDockRepository.getAll()
    }

}
